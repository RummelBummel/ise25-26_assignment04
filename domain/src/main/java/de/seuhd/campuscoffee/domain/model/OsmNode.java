package de.seuhd.campuscoffee.domain.model;

import lombok.Builder;
import org.jspecify.annotations.NonNull;

/**
 * Represents an OpenStreetMap node with relevant Point of Sale information.
 * This is the domain model for OSM data before it is converted to a POS object.
 */
@Builder
public record OsmNode(
        @NonNull Long nodeId,
        String name,
        String amenity,
        String street,
        String houseNumber,
        String postalCode,
        String city,
        Double latitude,
        Double longitude
) {}
