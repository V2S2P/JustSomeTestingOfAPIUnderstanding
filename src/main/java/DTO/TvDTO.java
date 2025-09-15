package DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.ToString;

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
