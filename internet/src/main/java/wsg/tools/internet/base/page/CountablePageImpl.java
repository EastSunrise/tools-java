package wsg.tools.internet.base.page;

import java.util.List;

/**
 * @author Kingen
 * @since 2021/3/26
 */
public class CountablePageImpl<T> extends AbstractPage<T> implements CountablePage<T> {

    private static final long serialVersionUID = 6416464593678531716L;

    private final int totalPages;

    /**
     * Constructs an instance of {@link Page} whose page amount is available.
     *
     * @param totalPages the total amount of pages, must be positive
     */
    protected CountablePageImpl(List<T> content, PageReq req, int totalPages) {
        super(content, req);
        if (totalPages < 1) {
            throw new IllegalArgumentException("Total pages must not be less than one!");
        }
        this.totalPages = totalPages;
    }

    /**
     * Constructs an instance of {@link Page} with a specified size.
     *
     * @param totalPages the total amount of pages, must be positive
     */
    protected CountablePageImpl(List<T> content, PageIndex index, int pageSize, int totalPages) {
        super(content, index, pageSize);
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
