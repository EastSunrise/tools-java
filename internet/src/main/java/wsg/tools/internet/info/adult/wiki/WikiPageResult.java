package wsg.tools.internet.info.adult.wiki;

import java.util.List;
import wsg.tools.internet.base.impl.BasicPageResult;
import wsg.tools.internet.base.intf.PageRequest;

/**
 * A paged result of {@link WikiCelebrityIndex}.
 *
 * @author Kingen
 * @since 2021/3/11
 */
public class WikiPageResult extends BasicPageResult<WikiCelebrityIndex> {

    public WikiPageResult(List<WikiCelebrityIndex> content, PageRequest pageRequest, long total) {
        super(content, pageRequest, total);
    }

    @Override
    public WikiPageRequest nextPageRequest() {
        return (WikiPageRequest) super.nextPageRequest();
    }

    @Override
    public WikiPageRequest previousPageRequest() {
        return (WikiPageRequest) super.previousPageRequest();
    }
}
