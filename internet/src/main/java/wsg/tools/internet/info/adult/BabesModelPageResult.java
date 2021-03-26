package wsg.tools.internet.info.adult;

import java.util.List;
import wsg.tools.internet.base.page.BasicPageResult;
import wsg.tools.internet.base.page.PageRequest;

/**
 * Pages result of {@link BabesModelIndex}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class BabesModelPageResult extends BasicPageResult<BabesModelIndex> {

    BabesModelPageResult(List<BabesModelIndex> content, PageRequest pageRequest, int totalPages) {
        super(content, pageRequest, totalPages);
    }

    @Override
    public BabesPageRequest nextPageRequest() {
        return (BabesPageRequest) super.nextPageRequest();
    }

    @Override
    public BabesPageRequest previousPageRequest() {
        return (BabesPageRequest) super.previousPageRequest();
    }
}
