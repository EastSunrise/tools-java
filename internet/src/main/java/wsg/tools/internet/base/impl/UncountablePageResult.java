package wsg.tools.internet.base.impl;

import java.util.List;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.intf.PageRequest;
import wsg.tools.internet.base.intf.PageResult;

/**
 * Basic implementation of {@link PageResult} with countable total pages instead of total elements.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class UncountablePageResult<T> extends AbstractPageResult<T> {

    private final int totalPages;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param content     the content of this page, must not be {@literal null}.
     * @param pageRequest the paging information, must not be {@literal null}.
     * @param totalPages  the total amount of pages, must be positive
     */
    public UncountablePageResult(List<T> content, PageRequest pageRequest, int totalPages) {
        super(content, pageRequest);
        this.totalPages = AssertUtils.requireRange(totalPages, 1, null);
    }

    @Override
    public int getTotalPages() {
        return totalPages;
    }
}
