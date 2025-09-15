# TMDb Fetcher Example (Java + JPA Project)

This project demonstrates how to fetch data from the [TMDb API](https://developer.themoviedb.org/) using Java’s `HttpClient` and map the JSON response into **DTO classes** with Jackson.

The example shows how to fetch a TV show by IMDb ID (e.g., `tt11126994` for *Arcane*).

---

## 📂 Project Structure

### 1. `TvDTO`
Represents **one TV show object** returned inside the `tv_results` array from the API.

```java
@Data
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class TvDTO {
    private int id;

    @JsonProperty("name")
    private String title;

    @JsonProperty("media_type")
    private String mediaType;

    private double popularity;

    @JsonProperty("vote_count")
    private double rating;
}
Explanation of annotations and fields:
```
```plaintext
Copy code
- @Data → Lombok annotation generating getters, setters, equals, hashCode, etc.
- @ToString → automatically creates a toString() method.
- @JsonIgnoreProperties(ignoreUnknown = true) → tells Jackson to ignore any JSON fields not declared in the class.
- @JsonProperty("name") → maps the JSON "name" field to the Java "title" field.
- @JsonProperty("vote_count") → maps the JSON "vote_count" field to the Java "rating" field.
Example JSON → Java Mapping:
```
```java
json
Copy code
{
  "id": 94605,
  "name": "Arcane",
  "media_type": "tv",
  "popularity": 17.8762,
  "vote_count": 5361
}

TvDTO {
  id = 94605,
  title = "Arcane",
  mediaType = "tv",
  popularity = 17.8762,
  rating = 5361
}
2. StructureDTO
Represents the outer structure of the API response.

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StructureDTO {
    @JsonProperty("tv_results")
    private List<TvDTO> tvDTOList;
}
```
Explanation:

```plaintext
Copy code
- The TMDb API response contains several arrays: movie_results, person_results, tv_results, etc.
- We only care about tv_results for this example.
- @JsonProperty("tv_results") ensures Jackson maps the JSON array to the Java field tvDTOList.
- After mapping, StructureDTO will hold a list of TvDTO objects.
Example JSON:

{
  "movie_results": [],
  "person_results": [],
  "tv_results": [
    {
      "id": 94605,
      "name": "Arcane",
      "media_type": "tv",
      "popularity": 17.8762,
      "vote_count": 5361
    }
  ],
  "tv_episode_results": [],
  "tv_season_results": []
}
```
Mapped Java Object:

```java
StructureDTO {
  tvDTOList = [ TvDTO{id=94605, title="Arcane", ...} ]
}
```
## 3. TmdbFetcher
Handles HTTP requests and JSON deserialization.
```java
public class TmdbFetcher {
    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();
    private final String apiKey = System.getenv("API_KEY"); // API key from environment variable

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
```
Explanation:
- HttpClient → built-in Java HTTP client for sending requests.
- ObjectMapper → Jackson object to map JSON strings to DTOs.
- apiKey → retrieved from system environment variable "API_KEY".
- findByImdbId(String imdbId):
  1. Builds the request URL using the IMDb ID.
  2. Creates an HTTP GET request.
  3. Sends the request and reads the response as a string.
  4. Maps the JSON response to StructureDTO if the status code is 200.
  5. Returns the populated StructureDTO.
## 4. Main
Test harness to demonstrate fetching and printing TV show data.
```java
public static void main(String[] args) {
    TmdbFetcher tmdbFetcher = new TmdbFetcher();

    StructureDTO result = tmdbFetcher.findByImdbId("tt11126994");
    if (result != null && result.getTvDTOList() != null) {
        result.getTvDTOList().forEach(System.out::println);
    }
}
```
Explanation:

```plaintext
- Creates a TmdbFetcher instance.
- Calls findByImdbId with IMDb ID tt11126994.
- Gets back a StructureDTO with a list of TvDTOs.
- Loops over the list and prints each TV show using the toString() method from @ToString.
Sample Output:

TvDTO(id=94605, title=Arcane, mediaType=tv, popularity=17.8762, rating=5361)
```

## 🔑 Setup
Set your TMDb API key as an environment variable:

export API_KEY=your_api_key_here
Run the Main class.

The output will display the fetched TV show information.

## ✅ Summary
plaintext
Copy code
- TvDTO → Represents a single TV show object.
- StructureDTO → Wraps the API response containing the tv_results list.
- TmdbFetcher → Fetches JSON from TMDb API and maps it to DTOs.
- Main → Demonstrates usage and prints TV show info.
Notes:

plaintext
Copy code
- DTOs are separate from Entities (for database storage via JPA).
- Environment variables keep your API key secure.
- Jackson annotations (@JsonProperty, @JsonIgnoreProperties) allow flexible mapping of API JSON fields to Java fields.
- The design cleanly separates data structures, API fetching, and execution/testing.