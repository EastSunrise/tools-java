package wsg.tools.internet.info.game;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import lombok.Getter;
import wsg.tools.common.jackson.deserializer.TimestampDeserializer;

/**
 * A record of the match.
 *
 * @author Kingen
 * @since 2021/3/12
 */
@Getter
public class MatchRecord {

    @JsonProperty("title")
    private String title;
    @JsonProperty("tournament_name")
    private String tournamentName;
    @JsonProperty("is_publist")
    private boolean publist;

    @JsonProperty("matchID")
    private long matchId;
    @JsonProperty("status")
    private ScoreStatus status;
    @JsonProperty("game_count")
    private int gameCount;
    @JsonProperty("team_a_win")
    private int teamWinA;
    @JsonProperty("team_b_win")
    private int teamWinB;
    @JsonProperty("match_date")
    private LocalDate matchDate;
    @JsonProperty("match_time")
    @JsonFormat(pattern = "HH:mm")
    private LocalTime matchTime;
    @JsonProperty("start_time")
    @JsonDeserialize(using = TimestampDeserializer.class)
    private LocalDateTime startTime;
    @JsonProperty("homesite")
    private String homesite;
    @JsonProperty("is_have_video_link")
    @JsonDeserialize(using = IntStringBooleanDeserializer.class)
    private boolean haveVideoLink;
    @JsonProperty("live_video_url1")
    private String liveVideoUrl1;

    @JsonProperty("teamID_a")
    private int teamIdA;
    @JsonProperty("teamID_b")
    private int teamIdB;
    @JsonProperty("team_short_name_b")
    private String teamShortNameB;
    @JsonProperty("team_short_name_a")
    private String teamShortNameA;
    @JsonProperty("team_image_thumb_b")
    private String teamImageThumbB;
    @JsonProperty("team_image_thumb_a")
    private String teamImageThumbA;
    @JsonProperty("homesite_a")
    private String homesiteA;
    @JsonProperty("homesite_b")
    private String homesiteB;

    MatchRecord() {
    }
}