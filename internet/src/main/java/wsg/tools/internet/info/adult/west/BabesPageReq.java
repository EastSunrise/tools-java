package wsg.tools.internet.info.adult.west;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.view.PathSupplier;

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
        private final String argument;

        VideoSortBy(String path, String argument) {
            this.path = path;
            this.argument = argument;
        }

        @Override
        public String getAsPath() {
            return path;
        }

        public String getArgument() {
            return argument;
        }
    }
}
