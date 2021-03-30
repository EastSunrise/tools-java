package wsg.tools.internet.base.page;

import java.util.List;

/**
 * Base implementation of {@link PageResult}.
 *
 * @author Kingen
 * @since 2021/3/26
 */
public class BasicPageResult<T, P extends PageReq> extends AbstractPageResult<T, P> {

    private final int totalPages;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param totalPages the total amount of pages, must be positive
     */
    public BasicPageResult(List<T> content, P request, int totalPages) {
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
}
