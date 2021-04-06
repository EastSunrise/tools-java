package wsg.tools.internet.info.adult.west;

import java.util.List;
import wsg.tools.internet.base.page.CountablePageResult;

/**
 * A paged result of indices of videos on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findPage(BabesPageReq)
 * @see BabesTubeSite#findPageByCategory(String, BabesPageReq)
 * @since 2021/4/3
 */
public class BabesPageResult extends CountablePageResult<BabesVideoIndex, BabesPageReq> {

    BabesPageResult(List<BabesVideoIndex> content, BabesPageReq request, int totalPages) {
        super(content, request, totalPages);
    }
}
