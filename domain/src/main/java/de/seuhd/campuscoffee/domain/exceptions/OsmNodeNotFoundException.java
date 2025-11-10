package de.seuhd.campuscoffee.domain.exceptions;

/**
 * Exception thrown when an OpenStreetMap node is not found and hence cannot be imported.
 */
public class OsmNodeNotFoundException extends RuntimeException {
    public OsmNodeNotFoundException(String posId) {
        super("The OpenStreetMap node with ID " + posId + " does not exist.");
    }
}
