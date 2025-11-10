package de.seuhd.campuscoffee.application.pos;

import de.seuhd.campuscoffee.application.osm.OsmDataServiceImpl;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.model.Pos;
import de.seuhd.campuscoffee.domain.model.PosType;
import de.seuhd.campuscoffee.domain.model.Address;
import de.seuhd.campuscoffee.domain.model.Campus;
import de.seuhd.campuscoffee.domain.ports.PosDataService;
import org.springframework.stereotype.Service;

/**
 * Application service that imports a POS from an OpenStreetMap node.
 *
 * Flow:
 *  - Fetch OSM node via OsmDataService
 *  - Map it to a POS domain object
 *  - Persist the POS using PosDataService
 */
@Service
public class OsmPosImportService {

    private final OsmDataServiceImpl osmDataService;
    private final PosDataService posDataService;

    public OsmPosImportService(OsmDataServiceImpl osmDataService,
                               PosDataService posDataService) {
        this.osmDataService = osmDataService;
        this.posDataService = posDataService;
    }

    public Pos importFromOsmNode(Long osmNodeId) throws OsmNodeNotFoundException {
        // 1. OSM Node laden
        OsmNode osmNode = osmDataService.fetchNode(osmNodeId);

        // 2. OSM → POS mappen
        Pos pos = mapOsmNodeToPos(osmNode);

        // 3. Speichern
        return posDataService.save(pos); // TODO: Methode ggf. anpassen (create/save/etc.)
    }

    private Pos mapOsmNodeToPos(OsmNode osm) {
        // TODO: An deine echte Pos-Klasse anpassen (Builder, Konstruktor, Setter, ...)
        // Beispiel: Wenn Pos einen Builder hat:
        Address address = Address.builder()
                .street(osm.street())
                .houseNumber(osm.houseNumber())
                .postalCode(osm.postalCode())
                .city(osm.city())
                .build();

        return Pos.builder()
                .name(osm.name())
                .type(mapAmenityToPosType(osm.amenity()))
                .address(address)
                .latitude(osm.latitude())
                .longitude(osm.longitude())
                .campus(Campus.ALTSTADT) // TODO: ggf. echte Campus-Logik einbauen
                .build();
    }

    private PosType mapAmenityToPosType(String amenity) {
        if (amenity == null) {
            return PosType.OTHER; // TODO: an vorhandenes Enum anpassen
        }

        return switch (amenity.toLowerCase()) {
            case "cafe" -> PosType.CAFE;
            case "restaurant" -> PosType.RESTAURANT;
            default -> PosType.OTHER;
        };
    }
}
