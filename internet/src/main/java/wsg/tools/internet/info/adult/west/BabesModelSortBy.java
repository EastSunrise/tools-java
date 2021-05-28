package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.view.PathSupplier;

/**
 * Available sorts when querying the models.
 *
 * @author Kingen
 * @since 2021/5/28
 */
public enum BabesModelSortBy implements PathSupplier {
    LAST_UPDATE("last_content_date"),
    MOST_VIEWED("model_viewed"),
    TOP_RATED("rating"),
    ALPHABETICALLY("title");

    private final String path;

    BabesModelSortBy(String path) {
        this.path = path;
    }

    @Override
    public String getAsPath() {
        return path;
    }
}
