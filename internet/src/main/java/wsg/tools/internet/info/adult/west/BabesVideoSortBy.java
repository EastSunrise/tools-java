package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.view.PathSupplier;

/**
 * Available sorts when querying videos.
 *
 * @author Kingen
 * @since 2021/5/28
 */
public enum BabesVideoSortBy implements PathSupplier {
    LAST_UPDATE("latest-updates", "post_date"),
    TOP_RATED("top-rated", "rating"),
    TOP_RATED_MONTHLY("top-rated", "rating_month"),
    TOP_RATED_WEEKLY("top-rated", "rating_week"),
    TOP_RATED_DAILY("top-rated", "rating_today"),
    MOST_VIEWED("most-popular", "video_viewed"),
    MOST_VIEWED_MONTHLY("most-popular", "video_viewed_month"),
    MOST_VIEWED_WEEKLY("most-popular", "video_viewed_week"),
    MOST_VIEWED_DAILY("most-popular", "video_viewed_today"),
    DURATION("longest", "duration");

    private final String path;
    private final String sortBy;

    BabesVideoSortBy(String path, String sortBy) {
        this.path = path;
        this.sortBy = sortBy;
    }

    @Override
    public String getAsPath() {
        return path;
    }

    public String getSortBy() {
        return sortBy;
    }
}
