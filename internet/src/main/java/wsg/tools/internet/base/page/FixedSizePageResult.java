package wsg.tools.internet.base.page;

import java.util.List;

/**
 * The page result with a fixed page size.
 *
 * @author Kingen
 * @see FixedSizePageReq
 * @since 2021/5/21
 */
public class FixedSizePageResult<T, P extends FixedSizePageReq>
    extends AbstractPageResult<T, P> implements PageCountable, AmountCountable {

    private final int pageSize;
    private final long total;

    public FixedSizePageResult(List<T> content, P req, long total, int pageSize) {
        super(content, req);
        this.pageSize = pageSize;
        if (!content.isEmpty()) {
            int offset = req.getCurrent() * pageSize;
            if (offset + pageSize > total) {
                total = offset + content.size();
            }
        }
        this.total = total;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    @Override
    public long getTotalElements() {
        return total;
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) total / (double) pageSize);
    }

    @Override
    public boolean hasNext() {
        return getCurrent() + 1 < getTotalPages();
    }
}
