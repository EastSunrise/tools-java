package wsg.tools.internet.info.game;

import java.util.List;
import wsg.tools.internet.base.page.AmountCountablePageResult;

/**
 * A paged result of tournaments from the {@link ScoreSite}.
 *
 * @author Kingen
 * @since 2021/3/13
 */
public class TournamentPageResult extends AmountCountablePageResult<Tournament, TournamentPageReq> {

    TournamentPageResult(List<Tournament> content, TournamentPageReq request, long total) {
        super(content, request, total);
    }
}