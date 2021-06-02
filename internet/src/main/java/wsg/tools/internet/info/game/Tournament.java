package wsg.tools.internet.info.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;

/**
 * A tournament.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public class Tournament {

    @JsonProperty("tournamentID")
    private int id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("short_name")
    private String shortName;
    @JsonProperty("image_thumb")
    private String imageThumb;
    @JsonProperty("start_date")
    private LocalDate startDate;
    @JsonProperty("end_date")
    private LocalDate endDate;
    @JsonProperty("status")
    private ScoreStatus status;

    Tournament() {
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public String getImageThumb() {
        return imageThumb;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public ScoreStatus getStatus() {
        return status;
    }
}
