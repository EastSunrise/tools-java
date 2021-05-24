package wsg.tools.internet.base.page;

import java.io.Serializable;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;

/**
 * The page request only with the page index. The page size is unspecified and may be returned by
 * the server.
 *
 * @author Kingen
 * @see FixedSizePageResult
 * @since 2021/5/20
 */
public class FixedSizePageReq implements PageReq, Serializable {

    private static final long serialVersionUID = -6711569047982652285L;

    private final int current;

    /**
     * Creates an instance of paged request.
     *
     * @param current zero-based page index, must not be negative.
     */
    public FixedSizePageReq(int current) {
        if (current < 0) {
            throw new IllegalArgumentException("Page index must not be less than zero!");
        }
        this.current = current;
    }

    @Nonnull
    @Contract(value = " -> new", pure = true)
    public static FixedSizePageReq first() {
        return new FixedSizePageReq(0);
    }

    @Override
    public int getCurrent() {
        return current;
    }

    @Override
    public int getPageSize() {
        throw new UnsupportedOperationException("Can't assign the size of the page");
    }

    @Override
    public PageReq next() {
        return new FixedSizePageReq(current + 1);
    }

    @Override
    public PageReq previous() {
        return current == 0 ? this : new FixedSizePageReq(current - 1);
    }
}
