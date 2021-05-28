package wsg.tools.internet.base.page;

import java.io.Serializable;

/**
 * A basic implementation of {@link PageIndex}.
 *
 * @author Kingen
 * @since 2021/5/27
 */
public class PageIndexImpl implements PageIndex, Serializable {

    private static final long serialVersionUID = -2271890872443538224L;

    private final int current;

    /**
     * Creates an instance of page request.
     *
     * @param current zero-based page index, must not be negative.
     */
    protected PageIndexImpl(int current) {
        if (current < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        this.current = current;
    }

    @Override
    public int getCurrent() {
        return current;
    }

    @Override
    public PageIndex next() {
        return new PageIndexImpl(current + 1);
    }

    @Override
    public PageIndex previous() {
        return current == 0 ? this : new PageIndexImpl(current - 1);
    }
}
