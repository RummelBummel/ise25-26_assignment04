package de.seuhd.campuscoffee.application.osm;

import de.seuhd.campuscoffee.domain.exceptions.OsmNodeNotFoundException;
import de.seuhd.campuscoffee.domain.model.OsmNode;
import de.seuhd.campuscoffee.domain.ports.OsmDataService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class OsmDataServiceImpl implements OsmDataService {

    private static final String OSM_API_URL = "https://api.openstreetmap.org/api/0.6/node/";

    private final OsmXmlParser parser = new OsmXmlParser();
    private final HttpClient client = HttpClient.newHttpClient();

    @Override
    public OsmNode fetchNode(Long nodeId) throws OsmNodeNotFoundException {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(OSM_API_URL + nodeId))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 404) {
                throw new OsmNodeNotFoundException("OSM node with ID " + nodeId + " not found.");
            }

            if (response.statusCode() != 200) {
                throw new OsmNodeNotFoundException(
                        "Failed to fetch OSM node: HTTP " + response.statusCode()
                );
            }

            return parser.parse(response.body());

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new OsmNodeNotFoundException("Error fetching OSM node: " + e.getMessage());
        }
    }
}
