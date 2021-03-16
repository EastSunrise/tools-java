package wsg.tools.internet.base.impl;

import java.util.List;
import wsg.tools.internet.base.intf.PageRequest;
import wsg.tools.internet.base.intf.PageResult;

/**
 * Basic implementation of {@link PageResult} with countable total elements.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class BasicPageResult<T> extends AbstractPageResult<T> {

    private final long total;

    /**
     * Constructs an instance of {@link PageResult}.
     *
     * @param total the total amount of elements available. The total might be adapted considering
     *              the length of the content given, if it is going to be the content of the last
     *              page.
     */
    public BasicPageResult(List<T> content, PageRequest pageRequest, long total) {
        super(content, pageRequest);
        if (!content.isEmpty()) {
            int offset = pageRequest.getCurrent() * pageRequest.getPageSize();
            if (offset + pageRequest.getPageSize() > total) {
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
    public long getTotalElements() {
        return total;
    }
}
