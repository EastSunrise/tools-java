package wsg.tools.internet.info.game;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import lombok.Getter;

/**
 * A round in a tournament, like the play-off of a spring tournament.
 *
 * @author Kingen
 * @since 2021/3/13
 */
@Getter
public class TournamentRound {

    @JsonProperty("roundID")
    private int roundId;
    @JsonProperty("r_type")
    private RoundType type;
    @JsonProperty("tournamentID")
    private int tournamentId;
    @JsonProperty("name")
    private String name;
    @JsonProperty("name_en")
    private String nameEn;
    @JsonProperty("is_now_week")
    private boolean nowWeek;
    @JsonProperty("is_use_tree")
    @JsonDeserialize(using = IntStringBooleanDeserializer.class)
    private boolean useTree;
    @JsonProperty("is_use_points")
    @JsonDeserialize(using = IntStringBooleanDeserializer.class)
    private boolean usePoints;

    @JsonProperty("round_son")
    private List<RoundItem> roundSon;

    TournamentRound() {
    }
}
