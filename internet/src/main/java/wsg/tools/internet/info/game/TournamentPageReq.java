package wsg.tools.internet.info.game;

import java.time.Year;
import javax.annotation.Nonnull;
import lombok.Getter;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.page.BasicPageReq;

/**
 * A paged request to query tournaments in the {@link ScoreSite}.
 *
 * @author Kingen
 * @since 2021/3/13
 */
@Getter
public class TournamentPageReq extends BasicPageReq {

    private static final long serialVersionUID = 4429545920348840055L;
    private static final Year START_YEAR = Year.of(2012);

    private final GameType game;
    private Year year;
    private ScoreStatus status;

    public TournamentPageReq(int current, int pageSize, @Nonnull GameType game, Year year,
        ScoreStatus status) {
        super(current, pageSize);
        this.game = game;
        if (year != null) {
            AssertUtils.requireRange(year, START_YEAR, Year.now().plusYears(1));
        }
        this.year = year;
        this.status = status;
    }

    /**
     * Creates an instance of paged request.
     *
     * @param current  zero-based page index, must not be negative.
     * @param pageSize the size of the page to be returned, must be greater than 0.
     */
    public TournamentPageReq(int current, int pageSize, @Nonnull GameType game) {
        super(current, pageSize);
        this.game = game;
    }

    @Nonnull
    @Contract("_, _ -> new")
    public static TournamentPageReq lol(int current, int pageSize) {
        return new TournamentPageReq(current, pageSize, GameType.LOL);
    }

    @Override
    public TournamentPageReq next() {
        BasicPageReq next = super.next();
        return new TournamentPageReq(next.getCurrent(), next.getPageSize(), game, year, status);
    }

    @Override
    public TournamentPageReq previous() {
        BasicPageReq prev = super.previous();
        return new TournamentPageReq(prev.getCurrent(), prev.getPageSize(), game, year, status);
    }
}
