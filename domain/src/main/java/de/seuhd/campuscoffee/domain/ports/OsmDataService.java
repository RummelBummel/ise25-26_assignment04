package de.seuhd.campuscoffee.domain.ports;

import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import org.jspecify.annotations.NonNull;

/**
 * Port for importing Point of Sale data from OpenStreetMap.
 * Defines how to fetch and parse OSM node data from the external OSM API.
 */
public interface OsmDataService {

    /**
     * Fetches an OpenStreetMap node by its ID and returns its parsed domain model.
     *
     * @param nodeId The OpenStreetMap node ID.
     * @return The parsed OsmNode with relevant tags and coordinates.
     * @throws OsmNodeNotFoundException if the node could not be retrieved or parsed.
     */
    @NonNull OsmNode fetchNode(@NonNull Long nodeId) throws OsmNodeNotFoundException;
}
