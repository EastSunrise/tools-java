package wsg.tools.internet.info.adult.midnight;

import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.base.impl.BasicPageRequest;

/**
 * An implementation of {@link wsg.tools.internet.base.intf.PageRequest} for {@link MidnightSite},
 * including a {@link OrderBy}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class MidnightPageRequest extends BasicPageRequest {

    private static final long serialVersionUID = -8443301620276250501L;
    private static final int MIN_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 80;

    private final OrderBy orderBy;

    public MidnightPageRequest(int current, int pageSize) {
        this(current, pageSize, OrderBy.UPDATE);
    }

    public MidnightPageRequest(int current, int pageSize, @Nonnull OrderBy orderBy) {
        super(current, pageSize);
        this.orderBy = orderBy;
    }

    /**
     * Returns an instance of {@link MidnightPageRequest} for the first page in the size of {@link
     * #MAX_PAGE_SIZE}, order by {@link OrderBy#UPDATE}.
     */
    public static MidnightPageRequest first() {
        return new MidnightPageRequest(0, MAX_PAGE_SIZE);
    }

    /**
     * Restricts the page size not larger than {@link #MAX_PAGE_SIZE} and not smaller than {@link
     * #MIN_PAGE_SIZE}.
     */
    @Override
    public int getPageSize() {
        int pageSize = super.getPageSize();
        return Math.max(MIN_PAGE_SIZE, Math.min(pageSize, MAX_PAGE_SIZE));
    }

    @Override
    public MidnightPageRequest next() {
        BasicPageRequest next = super.next();
        return new MidnightPageRequest(next.getCurrent(), next.getPageSize(), orderBy);
    }

    @Override
    public MidnightPageRequest previous() {
        BasicPageRequest previous = super.previous();
        return new MidnightPageRequest(previous.getCurrent(), previous.getPageSize(), orderBy);
    }

    OrderBy getOrderBy() {
        return orderBy;
    }

    enum OrderBy implements TextSupplier {
        /**
         * The count of likes
         */
        LIKE("diggtop"),
        /**
         * The count of browsing
         */
        CLICK("onclick"),
        /**
         * The update time
         */
        UPDATE("newstime");

        private final String text;

        OrderBy(String text) {
            this.text = text;
        }

        @Override
        public String getText() {
            return text;
        }
    }
}
