package wsg.tools.internet.info.game;

import java.io.Serializable;
import java.time.Year;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.lang.AssertUtils;

/**
 * A paged request to query tournaments in the {@link ScoreSite}.
 *
 * @author Kingen
 * @since 2021/3/13
 */
public class TournamentReq implements Serializable {

    private static final long serialVersionUID = 4429545920348840055L;
    private static final Year START_YEAR = Year.of(2012);

    private final GameType game;
    private Year year;
    private ScoreStatus status;

    public TournamentReq(@Nonnull GameType game, Year year, ScoreStatus status) {
        this.game = game;
        if (year != null) {
            AssertUtils.requireRange(year, START_YEAR, Year.now().plusYears(1));
        }
        this.year = year;
        this.status = status;
    }

    public TournamentReq(@Nonnull GameType game) {
        this.game = game;
    }

    @Nonnull
    @Contract(value = " -> new", pure = true)
    public static TournamentReq lol() {
        return new TournamentReq(GameType.LOL);
    }

    public GameType getGame() {
        return game;
    }

    public Year getYear() {
        return year;
    }

    public ScoreStatus getStatus() {
        return status;
    }
}
