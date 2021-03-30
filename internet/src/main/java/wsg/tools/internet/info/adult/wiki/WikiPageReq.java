package wsg.tools.internet.info.adult.wiki;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.page.PageReq;

/**
 * An implementation of {@link PageReq} for {@link CelebrityWikiSite}, with a fixed page size.
 *
 * @author Kingen
 * @since 2021/3/11
 */
public class WikiPageReq extends BasicPageReq {

    private static final long serialVersionUID = 2881971676332630439L;

    /**
     * Creates an instance of paged request.
     *
     * @param current zero-based page index, must not be negative.
     */
    public WikiPageReq(int current) {
        super(current, 100);
    }

    @Nonnull
    public static WikiPageReq first() {
        return new WikiPageReq(0);
    }

    @Override
    public WikiPageReq next() {
        return new WikiPageReq(super.next().getCurrent());
    }

    @Override
    public BasicPageReq previous() {
        return new WikiPageReq(super.previous().getCurrent());
    }
}
