package wsg.tools.internet.resource.movie;

import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.page.PageReq;

/**
 * An implementation of {@link PageReq} for {@link GrapeSite}, including a {@link OrderBy}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public class GrapeVodPageReq extends BasicPageReq {

    private static final long serialVersionUID = -3003781993499472386L;
    private static final int PAGE_SIZE = 35;

    private final OrderBy orderBy;

    public GrapeVodPageReq(int current) {
        this(current, OrderBy.TIME);
    }

    public GrapeVodPageReq(int current, @Nonnull OrderBy orderBy) {
        super(current, PAGE_SIZE);
        this.orderBy = orderBy;
    }

    /**
     * Returns an instance of {@link GrapeVodPageReq} for the first page, order by {@link
     * OrderBy#TIME}.
     */
    public static GrapeVodPageReq first() {
        return new GrapeVodPageReq(0);
    }

    @Override
    public GrapeVodPageReq next() {
        return new GrapeVodPageReq(super.next().getCurrent(), orderBy);
    }

    @Override
    public GrapeVodPageReq previous() {
        return new GrapeVodPageReq(super.previous().getCurrent(), orderBy);
    }

    OrderBy getOrderBy() {
        return orderBy;
    }

    enum OrderBy implements TextSupplier {
        /**
         * The add time
         */
        TIME("addtime"),
        /**
         * The hits
         */
        HITS("hits"),
        /**
         * The rating
         */
        RATING("gold");

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
