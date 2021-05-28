package wsg.tools.internet.info.adult.ggg;

import java.util.List;
import wsg.tools.internet.base.page.AbstractPage;
import wsg.tools.internet.base.page.PageIndex;

/**
 * A paged result with goods.
 *
 * @author Kingen
 * @see GggSite#findAll(GggReq, PageIndex)
 * @since 2021/4/13
 */
public class GggPageResult extends AbstractPage<GggGood> {

    private static final long serialVersionUID = -2654880614818958839L;

    GggPageResult(List<GggGood> content, PageIndex pageIndex) {
        super(content, pageIndex, 10);
    }

    @Override
    public boolean hasNext() {
        return hasContent();
    }
}
