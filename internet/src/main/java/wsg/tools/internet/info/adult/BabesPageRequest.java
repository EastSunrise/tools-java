package wsg.tools.internet.info.adult;

import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.base.page.BasicPageRequest;

/**
 * Paged request in the {@link BabesTubeSite} with a {@link #orderBy}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class BabesPageRequest extends BasicPageRequest {

    private static final long serialVersionUID = 4974957290873480780L;
    private static final int PAGE_SIZE = 40;

    private final OrderBy orderBy;

    public BabesPageRequest(int current, @Nonnull OrderBy orderBy) {
        super(current, PAGE_SIZE);
        this.orderBy = orderBy;
    }

    OrderBy getOrderBy() {
        return orderBy;
    }

    @Override
    public BabesPageRequest next() {
        BasicPageRequest next = super.next();
        return new BabesPageRequest(next.getCurrent(), orderBy);
    }

    @Override
    public BabesPageRequest previous() {
        BasicPageRequest previous = super.previous();
        return new BabesPageRequest(previous.getCurrent(), orderBy);
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
