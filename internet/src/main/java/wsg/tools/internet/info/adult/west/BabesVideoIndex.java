package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.info.adult.common.VideoQuality;
import wsg.tools.internet.info.adult.view.PreviewSupplier;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * An index pointing to a video on {@link BabesTubeSite}.
 *
 * @author Kingen
 * @see BabesTubeSite#findPage(BabesPageReq)
 * @see BabesTubeSite#findPageByCategory(String, BabesPageReq)
 * @see BabesPageResult
 * @since 2021/3/16
 */
public interface BabesVideoIndex
    extends IntIdentifier, PathSupplier, TitledAdultEntry, PreviewSupplier {

    /**
     * Returns the quality of the video.
     *
     * @return the quality
     */
    VideoQuality getQuality();

    /**
     * Returns the rating of the video.
     *
     * @return the rating
     */
    double getRating();

    /**
     * Returns the count of views of the video
     *
     * @return the count of views
     */
    int getViews();
}
