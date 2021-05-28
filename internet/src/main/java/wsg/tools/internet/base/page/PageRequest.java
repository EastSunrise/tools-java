package wsg.tools.internet.base.page;

/**
 * Basic implementation of {@link PageReq}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class PageRequest extends PageIndexImpl implements PageReq {

    private static final long serialVersionUID = -128117024824984036L;

    private final int size;

    /**
     * Creates an instance of page request with a specified size.
     *
     * @param current zero-based page index, must not be negative.
     * @param size    the size of the page to be returned, must be greater than 0.
     */
    protected PageRequest(int current, int size) {
        super(current);
        if (size < 1) {
            throw new IllegalArgumentException("Page size must not be less than one!");
        }
        this.size = size;
    }

    @Override
    public int getPageSize() {
        return size;
    }

    @Override
    public long getOffset() {
        return (long) getCurrent() * size;
    }

    @Override
    public PageReq next() {
        return new PageRequest(super.next().getCurrent(), size);
    }

    @Override
    public PageReq previous() {
        return new PageRequest(super.previous().getCurrent(), size);
    }
}
