package wsg.tools.internet.info.game;

import java.time.Year;
import javax.annotation.Nonnull;
import lombok.Getter;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.page.BasicPageRequest;

/**
 * A paged request to query tournaments in the {@link ScoreSite}.
 *
 * @author Kingen
 * @since 2021/3/13
 */
@Getter
public class TournamentPageRequest extends BasicPageRequest {

    private static final long serialVersionUID = 4429545920348840055L;
    private static final Year START_YEAR = Year.of(2012);

    private final GameType game;
    private Year year;
    private ScoreStatus status;

    public TournamentPageRequest(int current, int pageSize, @Nonnull GameType game, Year year,
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
    public TournamentPageRequest(int current, int pageSize, @Nonnull GameType game) {
        super(current, pageSize);
        this.game = game;
    }

    public static TournamentPageRequest lol(int current, int pageSize) {
        return new TournamentPageRequest(current, pageSize, GameType.LOL);
    }

    @Override
    public TournamentPageRequest next() {
        BasicPageRequest next = super.next();
        return new TournamentPageRequest(next.getCurrent(), next.getPageSize(), game, year, status);
    }

    @Override
    public TournamentPageRequest previous() {
        BasicPageRequest prev = super.previous();
        return new TournamentPageRequest(prev.getCurrent(), prev.getPageSize(), game, year, status);
    }
}
