package wsg.tools.internet.info.adult.wiki;

import wsg.tools.internet.base.impl.BasicPageRequest;

/**
 * An implementation of {@link wsg.tools.internet.base.intf.PageRequest} for {@link
 * CelebrityWikiSite}, with a fixed page size.
 *
 * @author Kingen
 * @since 2021/3/11
 */
public class WikiPageRequest extends BasicPageRequest {

    private static final long serialVersionUID = 2881971676332630439L;

    /**
     * Creates an instance of paged request.
     *
     * @param current zero-based page index, must not be negative.
     */
    public WikiPageRequest(int current) {
        super(current, 100);
    }

    @Override
    public WikiPageRequest next() {
        return new WikiPageRequest(super.next().getCurrent());
    }

    @Override
    public BasicPageRequest previous() {
        return new WikiPageRequest(super.previous().getCurrent());
    }
}
