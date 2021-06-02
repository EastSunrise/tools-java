package wsg.tools.internet.movie.online;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author Kingen
 * @since 2021/5/30
 */
class SearchResult {

    @JsonProperty("error")
    private String error;
    @JsonProperty("status")
    private int status;
    @JsonProperty("result")
    private List<SearchedItem> items;

    SearchResult() {
    }

    public List<SearchedItem> getItems() {
        return items;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

}