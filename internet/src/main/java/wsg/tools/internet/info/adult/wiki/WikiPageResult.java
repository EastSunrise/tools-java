package wsg.tools.internet.info.adult.wiki;

import java.util.List;
import wsg.tools.internet.base.page.BasicCountablePageResult;

/**
 * A paged result of {@link WikiCelebrityIndex}.
 *
 * @author Kingen
 * @since 2021/3/11
 */
public class WikiPageResult extends BasicCountablePageResult<WikiCelebrityIndex, WikiPageReq> {

    public WikiPageResult(List<WikiCelebrityIndex> content, WikiPageReq request, long total) {
        super(content, request, total);
    }
}
