package wsg.tools.internet.info.adult.west;

import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.base.page.BasicPageReq;

/**
 * Paged request in the {@link BabesTubeSite} with a {@link #orderBy}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class BabesPageReq extends BasicPageReq {

    private static final long serialVersionUID = 4974957290873480780L;
    private static final int PAGE_SIZE = 40;

    private final OrderBy orderBy;

    public BabesPageReq(int current, @Nonnull OrderBy orderBy) {
        super(current, PAGE_SIZE);
        this.orderBy = orderBy;
    }

    OrderBy getOrderBy() {
        return orderBy;
    }

    @Override
    public BabesPageReq next() {
        BasicPageReq next = super.next();
        return new BabesPageReq(next.getCurrent(), orderBy);
    }

    @Override
    public BabesPageReq previous() {
        BasicPageReq previous = super.previous();
        return new BabesPageReq(previous.getCurrent(), orderBy);
    }

    enum OrderBy implements TextSupplier {
        /**
         * Four orders
         */
        LAST_UPDATE("last_content_date"),
        MOST_VIEWED("model_viewed"),
        TOP_RATED("rating"),
        ALPHABETICALLY("title");

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
