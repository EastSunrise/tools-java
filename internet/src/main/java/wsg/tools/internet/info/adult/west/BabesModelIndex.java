package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CoverSupplier;

/**
 * An index pointing to a model in the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findModelPage(BabesModelPageReq)
 * @see BabesModelPageResult
 * @since 2021/3/15
 */
public interface BabesModelIndex extends PathSupplier, CoverSupplier {

    /**
     * Returns the name of the model.
     *
     * @return the name
     */
    String getName();

    /**
     * Returns the count of videos cast by the model.
     *
     * @return the count of videos
     */
    int getVideos();

    /**
     * Returns the count of photos of the model.
     *
     * @return the count of photos
     */
    int getPhotos();
}
