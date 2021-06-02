package wsg.tools.internet.info.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/**
 * The response when requesting for tournaments from the {@link ScoreSite}.
 *
 * @author Kingen
 * @since 2021/3/13
 */
class TournamentPageResponse {

    @JsonProperty("code")
    private int statusCode;
    @JsonProperty("message")
    private String message;

    @JsonProperty("data")
    private TournamentData data;

    @JsonProperty("badge")
    private List<Object> badge;
    @JsonProperty("task_data")
    private Object taskData;
    @JsonProperty("event")
    private List<Object> event;

    public int getStatusCode() {
        return statusCode;
    }

    public String getMessage() {
        return message;
    }

    public TournamentData getData() {
        return data;
    }

    public List<Object> getBadge() {
        return badge;
    }

    public Object getTaskData() {
        return taskData;
    }

    public List<Object> getEvent() {
        return event;
    }

    static class TournamentData {

        @JsonProperty("count")
        private long count;
        @JsonProperty("list")
        private List<Tournament> list;
        @JsonProperty("page")
        private String page;
        @JsonProperty("year")
        private List<String> year;

        public long getCount() {
            return count;
        }

        public List<Tournament> getList() {
            return list;
        }

        public String getPage() {
            return page;
        }

        public List<String> getYear() {
            return year;
        }
    }
}
