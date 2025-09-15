package Service;

import DTO.StructureDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TmdbFetcher {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey = System.getenv("API_KEY"); // ✅ env var

    public StructureDTO findByImdbId(String imdbId) {
        try {
            String uri = "https://api.themoviedb.org/3/find/" + imdbId
                    + "?api_key=" + apiKey
                    + "&external_source=imdb_id";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(uri))
                    .header("Accept", "application/json")
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                return mapper.readValue(response.body(), StructureDTO.class);
            } else {
                System.out.println("Request failed: " + response.statusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
