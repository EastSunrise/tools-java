package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author Kingen
 * @since 2021/5/21
 */
class OmdbSearchResult extends OmdbResponse {

    @JsonProperty("Search")
    private List<AbstractOmdbMovie> items;
    @JsonProperty("totalResults")
    private Integer total;

    OmdbSearchResult() {
    }

    List<AbstractOmdbMovie> getItems() {
        return items;
    }

    Integer getTotal() {
        return total;
    }
}
