package wsg.tools.internet.info.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDate;
import lombok.Getter;

/**
 * A tournament.
 *
 * @author Kingen
 * @since 2021/3/12
 */
@Getter
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
}
