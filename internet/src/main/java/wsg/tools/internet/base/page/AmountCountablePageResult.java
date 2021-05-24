package wsg.tools.internet.base.page;

import java.util.List;

/**
 * An implementation of {@code PageResult} with the total amount of elements.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public class AmountCountablePageResult<T, P extends PageReq> extends AbstractPageResult<T, P>
    implements PageCountable, AmountCountable {

    private final long total;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param total the total amount of elements available. The total might be adapted considering
     *              the length of the content given, if it is going to be the content of the last
     *              page.
     */
    public AmountCountablePageResult(List<T> content, P req, long total) {
        super(content, req);
        if (!content.isEmpty()) {
            int offset = req.getCurrent() * req.getPageSize();
            if (offset + req.getPageSize() > total) {
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
