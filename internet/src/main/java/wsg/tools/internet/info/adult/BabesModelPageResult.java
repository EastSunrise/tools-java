package wsg.tools.internet.info.adult;

import java.util.List;
import wsg.tools.internet.base.page.BasicPageResult;

/**
 * Pages result of {@link BabesModelIndex}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class BabesModelPageResult extends BasicPageResult<BabesModelIndex, BabesPageReq> {

    BabesModelPageResult(List<BabesModelIndex> content, BabesPageReq request, int totalPages) {
        super(content, request, totalPages);
    }
}
