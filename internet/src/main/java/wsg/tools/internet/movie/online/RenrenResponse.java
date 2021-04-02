package wsg.tools.internet.movie.online;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * The response from the site.
 *
 * @author Kingen
 * @since 2021/3/22
 */
class RenrenResponse {

    @JsonProperty("result")
    private Result result;
    @JsonProperty("error")
    private String error;
    @JsonProperty("status")
    private int status;

    RenrenResponse() {
    }

    public Result getResult() {
        return result;
    }

    public String getError() {
        return error;
    }

    public int getStatus() {
        return status;
    }

    static class Result {

        @JsonProperty("end")
        private boolean end;
        @JsonProperty("content")
        private List<RenrenSeriesIndex> content;

        Result() {
        }

        public boolean isEnd() {
            return end;
        }

        public List<RenrenSeriesIndex> getContent() {
            return content;
        }
    }
}
