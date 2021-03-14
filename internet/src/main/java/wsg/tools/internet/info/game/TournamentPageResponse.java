package wsg.tools.internet.info.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Getter;

/**
 * The response when requesting for tournaments from the {@link ScoreSite}.
 *
 * @author Kingen
 * @since 2021/3/13
 */
@Getter
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

    @Getter
    static class TournamentData {

        @JsonProperty("count")
        private long count;
        @JsonProperty("list")
        private List<Tournament> list;
        @JsonProperty("page")
        private String page;
        @JsonProperty("year")
        private List<String> year;
    }
}
