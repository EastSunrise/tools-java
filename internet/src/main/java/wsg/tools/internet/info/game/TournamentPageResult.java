package wsg.tools.internet.info.game;

import java.util.List;
import wsg.tools.internet.base.impl.BasicPageResult;

/**
 * A paged result of tournaments from the {@link ScoreSite}.
 *
 * @author Kingen
 * @since 2021/3/13
 */
public class TournamentPageResult extends BasicPageResult<Tournament> {

    TournamentPageResult(List<Tournament> content, TournamentPageRequest pageRequest, long total) {
        super(content, pageRequest, total);
    }

    @Override
    public TournamentPageRequest nextPageRequest() {
        return (TournamentPageRequest) super.nextPageRequest();
    }

    @Override
    public TournamentPageRequest previousPageRequest() {
        return (TournamentPageRequest) super.previousPageRequest();
    }
}