package wsg.tools.internet.base.page;

import java.util.List;

/**
 * An implementation of {@code PageResult} with the number of total pages.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public class CountablePageResult<T, P extends PageReq> extends AbstractPageResult<T, P>
    implements PageCountable {

    private final int totalPages;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param totalPages the total amount of pages, must be positive
     */
    public CountablePageResult(List<T> content, P request, int totalPages) {
        super(content, request);
        if (totalPages < 1) {
            throw new IllegalArgumentException("Total pages must not be less than one!");
        }
        this.totalPages = totalPages;
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }

    @Override
    public boolean hasNext() {
        return getCurrent() + 1 < totalPages;
    }
}
