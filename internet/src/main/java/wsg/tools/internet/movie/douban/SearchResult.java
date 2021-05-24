package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * @author Kingen
 * @since 2021/5/15
 */
class SearchResult {

    @JsonProperty("items")
    private List<String> items;

    @JsonProperty("total")
    private int total;

    @JsonProperty("limit")
    private int limit;

    @JsonProperty("more")
    private boolean more;

    @JsonProperty("msg")
    private String msg;

    List<String> getItems() {
        return items;
    }

    int getTotal() {
        return total;
    }

    int getLimit() {
        return limit;
    }

    boolean isMore() {
        return more;
    }

    String getMsg() {
        return msg;
    }
}
