package wsg.tools.internet.base.page;

import java.util.List;

/**
 * @author Kingen
 * @since 2021/3/26
 */
public class AmountCountablePageImpl<T> extends AbstractPage<T> implements AmountCountablePage<T> {

    private static final long serialVersionUID = -1327122037758573539L;

    private final long total;

    /**
     * Constructs an instance of {@link Page} whose total amount is available.
     *
     * @param total the total amount of elements available. The total might be adapted considering
     *              the length of the content given, if it is going to be the content of the last
     *              page.
     */
    protected AmountCountablePageImpl(List<T> content, PageReq req, long total) {
        super(content, req);
        if (!content.isEmpty()) {
            int offset = req.getCurrent() * req.getPageSize();
            if (offset + req.getPageSize() > total) {
                total = offset + content.size();
            }
        }
        this.total = total;
    }

    /**
     * Constructs an instance of {@link Page} with a specified size.
     *
     * @param total the total amount of elements available.
     */
    protected AmountCountablePageImpl(List<T> content, PageIndex index, int pageSize, long total) {
        super(content, index, pageSize);
        if (!content.isEmpty()) {
            int offset = index.getCurrent() * pageSize;
            if (offset + pageSize > total) {
                total = offset + content.size();
            }
        }
        this.total = total;
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) total / (double) getPageSize());
    }

    @Override
    public long getTotalElements() {
        return total;
    }

    @Override
    public boolean hasNext() {
        return getCurrent() + 1 < getTotalPages();
    }
}
