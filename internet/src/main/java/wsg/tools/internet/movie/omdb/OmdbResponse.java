package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * The common response of OMDb API.
 *
 * @author Kingen
 * @since 2021/5/21
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "Type")
@JsonSubTypes({
    @JsonSubTypes.Type(value = OmdbMovie.class, name = "movie"),
    @JsonSubTypes.Type(value = OmdbSeries.class, name = "series"),
    @JsonSubTypes.Type(value = OmdbEpisode.class, name = "episode")
})
class OmdbResponse {

    @JsonProperty("Response")
    private boolean success;
    @JsonProperty("Error")
    private String error;

    OmdbResponse() {
    }

    boolean isSuccess() {
        return success;
    }

    String getError() {
        return error;
    }
}
