package wsg.tools.internet.info.adult.wiki;

import java.util.List;
import wsg.tools.internet.base.page.AmountCountablePageResult;

/**
 * A paged result of {@link WikiCelebrityIndex}.
 *
 * @author Kingen
 * @since 2021/3/11
 */
public class WikiPageResult extends AmountCountablePageResult<WikiCelebrityIndex, WikiPageReq> {

    public WikiPageResult(List<WikiCelebrityIndex> content, WikiPageReq request, long total) {
        super(content, request, total);
    }
}
