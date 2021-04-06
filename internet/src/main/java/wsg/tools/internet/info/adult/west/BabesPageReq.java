package wsg.tools.internet.info.adult.west;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.PathSupplier;
import wsg.tools.internet.base.page.BasicPageReq;

/**
 * A request with pagination information for videos on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findPage(BabesPageReq)
 * @see BabesTubeSite#findPageByCategory(String, BabesPageReq)
 * @since 2021/3/15
 */
public class BabesPageReq extends BasicPageReq {

    private static final long serialVersionUID = 4974957290873480780L;
    private static final int DEFAULT_SIZE = 50;

    private final VideoSortBy sortBy;

    public BabesPageReq(int current, @Nonnull VideoSortBy sortBy) {
        super(current, DEFAULT_SIZE);
        this.sortBy = sortBy;
    }

    @Nonnull
    @Contract(" -> new")
    public static BabesPageReq first() {
        return new BabesPageReq(0, VideoSortBy.LAST_UPDATE);
    }

    @Override
    public BabesPageReq next() {
        return new BabesPageReq(super.next().getCurrent(), sortBy);
    }

    @Override
    public BabesPageReq previous() {
        return new BabesPageReq(super.previous().getCurrent(), sortBy);
    }

    public VideoSortBy getSortBy() {
        return sortBy;
    }

    public enum VideoSortBy implements PathSupplier {
        LAST_UPDATE("post_date"),
        TOP_RATED("rating"),
        TOP_RATED_MONTHLY("rating_month"),
        TOP_RATED_WEEKLY("rating_week"),
        TOP_RATED_DAILY("rating_today"),
        MOST_VIEWED("video_viewed"),
        MOST_VIEWED_MONTHLY("video_viewed_month"),
        MOST_VIEWED_WEEKLY("video_viewed_week"),
        MOST_VIEWED_DAILY("video_viewed_today"),
        DURATION("duration");

        private final String path;

        VideoSortBy(String path) {
            this.path = path;
        }

        @Override
        public String getAsPath() {
            return path;
        }
    }
}
