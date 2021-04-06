package wsg.tools.internet.info.adult.west;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.PathSupplier;
import wsg.tools.internet.base.page.BasicPageReq;

/**
 * A request with pagination information for models on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findModelPage(BabesModelPageReq)
 * @since 2021/4/3
 */
public class BabesModelPageReq extends BasicPageReq {

    private static final long serialVersionUID = 1220369159992864185L;
    private static final int DEFAULT_SIZE = 40;

    private final ModelSortBy sortBy;

    public BabesModelPageReq(int current, @Nonnull ModelSortBy sortBy) {
        super(current, DEFAULT_SIZE);
        this.sortBy = sortBy;
    }

    public static BabesModelPageReq first() {
        return new BabesModelPageReq(0, ModelSortBy.LAST_UPDATE);
    }

    @Override
    public BabesModelPageReq next() {
        return new BabesModelPageReq(super.next().getCurrent(), sortBy);
    }

    @Override
    public BabesModelPageReq previous() {
        return new BabesModelPageReq(super.previous().getCurrent(), sortBy);
    }

    public ModelSortBy getSortBy() {
        return sortBy;
    }

    public enum ModelSortBy implements PathSupplier {
        LAST_UPDATE("last_content_date"),
        MOST_VIEWED("model_viewed"),
        TOP_RATED("rating"),
        ALPHABETICALLY("title");

        private final String path;

        ModelSortBy(String path) {
            this.path = path;
        }

        @Override
        public String getAsPath() {
            return path;
        }
    }
}
