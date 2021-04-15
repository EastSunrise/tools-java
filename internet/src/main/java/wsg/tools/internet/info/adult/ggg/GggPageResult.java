package wsg.tools.internet.info.adult.ggg;

import java.util.List;
import wsg.tools.internet.base.page.AbstractPageResult;

/**
 * A paged result with goods.
 *
 * @author Kingen
 * @see GggSite#findPage(GggPageReq)
 * @since 2021/4/13
 */
public class GggPageResult extends AbstractPageResult<GggGood, GggPageReq> {

    GggPageResult(List<GggGood> content, GggPageReq request) {
        super(content, request);
    }

    @Override
    public boolean hasNext() {
        return hasContent();
    }
}
