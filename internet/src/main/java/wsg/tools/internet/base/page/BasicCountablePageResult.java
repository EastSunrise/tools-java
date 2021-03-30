package wsg.tools.internet.base.page;

import java.util.List;

/**
 * Base implementation of {@link CountablePageResult}.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public class BasicCountablePageResult<T, P extends PageReq> extends AbstractPageResult<T, P>
    implements CountablePageResult<T, P> {

    private final long total;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param total the total amount of elements available. The total might be adapted considering
     *              the length of the content given, if it is going to be the content of the last
     *              page.
     */
    public BasicCountablePageResult(List<T> content, P request, long total) {
        super(content, request);
        if (!content.isEmpty()) {
            int offset = request.getCurrent() * request.getPageSize();
            if (offset + request.getPageSize() > total) {
                total = offset + content.size();
            }
        }
        this.total = total;
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) total / (double) getPageSize());
    }

    /**
     * Returns the total amount of elements.
     *
     * @return the total amount of elements
     */
    @Override
    public long getTotalElements() {
        return total;
    }
}
