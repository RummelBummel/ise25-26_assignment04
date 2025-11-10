package de.seuhd.campuscoffee.application.osm;

import de.seuhd.campuscoffee.domain.model.OsmNode;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Very simple XML parser for OSM node XML.
 * This avoids external XML libraries and only uses String/regex.
 * It assumes the typical OpenStreetMap node XML structure.
 */
public class OsmXmlParser {

    public OsmNode parse(String xmlContent) {
        if (xmlContent == null || xmlContent.isBlank()) {
            throw new RuntimeException("Empty OSM XML content.");
        }

        Long id = parseNodeAttributeAsLong(xmlContent, "id");
        Double lat = parseNodeAttributeAsDouble(xmlContent, "lat");
        Double lon = parseNodeAttributeAsDouble(xmlContent, "lon");

        String name = parseTagValue(xmlContent, "name");
        String amenity = parseTagValue(xmlContent, "amenity");
        String street = parseTagValue(xmlContent, "addr:street");
        String houseNumber = parseTagValue(xmlContent, "addr:housenumber");
        String postcode = parseTagValue(xmlContent, "addr:postcode");
        String city = parseTagValue(xmlContent, "addr:city");

        return OsmNode.builder()
                .nodeId(id)
                .name(name)
                .amenity(amenity)
                .street(street)
                .houseNumber(houseNumber)
                .postalCode(postcode)
                .city(city)
                .latitude(lat)
                .longitude(lon)
                .build();
    }

    private Long parseNodeAttributeAsLong(String xml, String attrName) {
        String value = parseNodeAttribute(xml, attrName);
        if (value == null) {
            return null;
        }
        return Long.parseLong(value);
    }

    private Double parseNodeAttributeAsDouble(String xml, String attrName) {
        String value = parseNodeAttribute(xml, attrName);
        if (value == null) {
            return null;
        }
        return Double.parseDouble(value);
    }

    /**
     * Extracts an attribute from the <node ...> element, e.g. id="1234".
     */
    private String parseNodeAttribute(String xml, String attrName) {
        // Match: <node ... attrName="value" ...>
        Pattern pattern = Pattern.compile(attrName + "=\"([^\"]*)\"");
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    /**
     * Extracts the v="..." of a tag like:
     * <tag k="name" v="My Cafe"/>
     */
    private String parseTagValue(String xml, String key) {
        // Match: <tag k="key" v="some value" .../>
        Pattern pattern = Pattern.compile(
                "<tag\\s+[^>]*k=\"" + Pattern.quote(key) + "\"\\s+[^>]*v=\"([^\"]*)\"[^>]*/?>"
        );
        Matcher matcher = pattern.matcher(xml);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
