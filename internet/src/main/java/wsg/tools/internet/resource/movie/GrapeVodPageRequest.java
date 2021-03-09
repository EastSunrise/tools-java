package wsg.tools.internet.resource.movie;

import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.base.impl.BasicPageRequest;

/**
 * An implementation of {@link wsg.tools.internet.base.intf.PageRequest} for {@link GrapeSite},
 * including a {@link OrderBy}.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public class GrapeVodPageRequest extends BasicPageRequest {

    private static final long serialVersionUID = -3003781993499472386L;
    private static final int PAGE_SIZE = 35;

    private final OrderBy orderBy;

    public GrapeVodPageRequest(int current) {
        this(current, OrderBy.TIME);
    }

    public GrapeVodPageRequest(int current, @Nonnull OrderBy orderBy) {
        super(current, PAGE_SIZE);
        this.orderBy = orderBy;
    }

    /**
     * Returns an instance of {@link GrapeVodPageRequest} for the first page, order by {@link
     * OrderBy#TIME}.
     */
    public static GrapeVodPageRequest first() {
        return new GrapeVodPageRequest(0);
    }

    @Override
    public GrapeVodPageRequest next() {
        return new GrapeVodPageRequest(super.next().getCurrent(), orderBy);
    }

    @Override
    public GrapeVodPageRequest previous() {
        return new GrapeVodPageRequest(super.previous().getCurrent(), orderBy);
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
