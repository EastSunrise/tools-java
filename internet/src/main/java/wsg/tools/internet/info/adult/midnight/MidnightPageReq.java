package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDate;
import javax.annotation.Nonnull;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.page.PageReq;

/**
 * An implementation of {@link PageReq} for {@link MidnightSite}, including a {@link OrderBy}.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class MidnightPageReq extends BasicPageReq {

    private static final long serialVersionUID = -8443301620276250501L;
    private static final int MIN_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 80;

    private final MidnightColumn column;
    private final LocalDate start;
    private final LocalDate end;
    private final OrderBy orderBy;

    public MidnightPageReq(int current, int pageSize, @Nonnull MidnightColumn column,
        @Nonnull OrderBy orderBy) {
        this(current, pageSize, column, null, null, orderBy);
    }

    public MidnightPageReq(int current, int pageSize, @Nonnull MidnightColumn column,
        LocalDate start, LocalDate end, @Nonnull OrderBy orderBy) {
        super(current, pageSize);
        AssertUtils.requireRange(pageSize, MIN_PAGE_SIZE, MAX_PAGE_SIZE + 1);
        this.column = column;
        this.start = start;
        this.end = end;
        this.orderBy = orderBy;
    }

    /**
     * Returns an instance of {@link MidnightPageReq} for the first page in the size of {@code
     * #MAX_PAGE_SIZE}, order by {@link OrderBy#UPDATE}.
     */
    @Nonnull
    public static MidnightPageReq first(MidnightColumn column) {
        return new MidnightPageReq(0, MAX_PAGE_SIZE, column, OrderBy.UPDATE);
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
    public MidnightPageReq next() {
        BasicPageReq next = super.next();
        return new MidnightPageReq(next.getCurrent(), next.getPageSize(), column, start, end,
            orderBy);
    }

    @Override
    public MidnightPageReq previous() {
        BasicPageReq previous = super.previous();
        return new MidnightPageReq(previous.getCurrent(), previous.getPageSize(), column, start,
            end, orderBy);
    }

    public MidnightColumn getColumn() {
        return column;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public OrderBy getOrderBy() {
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
