package de.seuhd.campuscoffee.domain.impl;

import de.seuhd.campuscoffee.domain.exceptions.DuplicatePosNameException;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeMissingFieldsException;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.exceptions.PosNotFoundException;
import de.seuhd.campuscoffee.domain.model.Address;
import de.seuhd.campuscoffee.domain.model.Campus;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.PosType;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import de.seuhd.campuscoffee.domain.ports.PosDataService;
import de.seuhd.campuscoffee.domain.ports.PosService;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Default implementation of the PosService port.
 * Orchestrates business logic for POS operations and delegates
 * persistence concerns to {@link PosDataService} and OSM access to {@link OsmDataService}.
 */
@Service
@RequiredArgsConstructor
public class PosServiceImpl implements PosService {

    private final PosDataService posDataService;
    private final OsmDataService osmDataService;

    @Override
    public void clear() {
        posDataService.clear();
    }

    @Override
    public @NonNull List<Pos> getAll() {
        return posDataService.getAll();
    }

    @Override
    public @NonNull Pos getById(@NonNull Long id) throws PosNotFoundException {
        return posDataService.getById(id);
    }

    @Override
    public @NonNull Pos upsert(@NonNull Pos pos)
            throws PosNotFoundException, DuplicatePosNameException {
        return posDataService.upsert(pos);
    }

    @Override
    public @NonNull Pos importFromOsmNode(@NonNull Long nodeId)
            throws OsmNodeNotFoundException, OsmNodeMissingFieldsException, DuplicatePosNameException {

        // 1. OSM-Node laden (kann OsmNodeNotFoundException werfen)
        OsmNode osmNode = osmDataService.fetchNode(nodeId);

        // 2. Pflichtfelder prüfen
        validateRequiredOsmFields(osmNode);

        // 3. OSM → POS mappen
        Pos pos = mapOsmNodeToPos(osmNode);

        // 4. Persistieren via upsert (kann DuplicatePosNameException werfen)
        return posDataService.upsert(pos);
    }

    /**
     * Validates that the OSM node contains all required fields for a valid POS.
     *
     * @param osmNode the OSM node data
     * @throws OsmNodeMissingFieldsException if any required field is missing or blank
     */
    private void validateRequiredOsmFields(OsmNode osmNode) throws OsmNodeMissingFieldsException {
        StringBuilder missing = new StringBuilder();

        if (isBlank(osmNode.name())) {
            missing.append("name, ");
        }
        if (isBlank(osmNode.amenity())) {
            missing.append("amenity, ");
        }
        if (isBlank(osmNode.street())) {
            missing.append("addr:street, ");
        }
        if (isBlank(osmNode.houseNumber())) {
            missing.append("addr:housenumber, ");
        }
        if (isBlank(osmNode.postalCode())) {
            missing.append("addr:postcode, ");
        }
        if (isBlank(osmNode.city())) {
            missing.append("addr:city, ");
        }
        if (osmNode.latitude() == null || osmNode.longitude() == null) {
            missing.append("lat/lon, ");
        }

        if (missing.length() > 0) {
            // letztes Komma/Leerzeichen entfernen
            String missingFields = missing.substring(0, missing.length() - 2);
            throw new OsmNodeMissingFieldsException(
                    "OSM node " + osmNode.nodeId() +
                            " is missing required fields: " + missingFields
            );
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    /**
     * Maps an OsmNode to a Pos domain object.
     * This encapsulates the mapping heuristic described in AGENTS.md.
     */
    private Pos mapOsmNodeToPos(OsmNode osmNode) {
        // ⚠️ Falls deine Address-Klasse anders gebaut wird, hier anpassen.
        Address address = Address.builder()
                .street(osmNode.street())
                .houseNumber(osmNode.houseNumber())
                .postalCode(osmNode.postalCode())
                .city(osmNode.city())
                .build();

        // ⚠️ Falls Pos kein Builder hat, sondern z.B. Konstruktor, entsprechend ändern.
        return Pos.builder()
                .name(osmNode.name())
                .type(mapAmenityToPosType(osmNode.amenity()))
                .address(address)
                .latitude(osmNode.latitude())
                .longitude(osmNode.longitude())
                // Campus-Heuristik: Default ALTSTADT, siehe AGENTS.md
                .campus(Campus.ALTSTADT)
                .build();
    }

    /**
     * Maps OSM amenity tag to the internal PosType enum.
     */
    private PosType mapAmenityToPosType(String amenity) {
        if (amenity == null) {
            return PosType.OTHER;
        }
        return switch (amenity.toLowerCase()) {
            case "cafe" -> PosType.CAFE;
            case "restaurant" -> PosType.RESTAURANT;
            case "fast_food" -> PosType.RESTAURANT;
            default -> PosType.OTHER;
        };
    }
}
