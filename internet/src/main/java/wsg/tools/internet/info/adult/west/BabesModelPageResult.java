package wsg.tools.internet.info.adult.west;

import java.util.List;
import wsg.tools.internet.base.page.CountablePageResult;

/**
 * A paged result of indices of models on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findModelPage(BabesModelPageReq)
 * @since 2021/3/15
 */
public class BabesModelPageResult extends CountablePageResult<BabesModelIndex, BabesModelPageReq> {

    BabesModelPageResult(List<BabesModelIndex> content, BabesModelPageReq request, int totalPages) {
        super(content, request, totalPages);
    }
}
