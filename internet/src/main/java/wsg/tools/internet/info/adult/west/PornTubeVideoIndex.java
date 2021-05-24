package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.TitleSupplier;

/**
 * An index pointing to a video on the site.
 *
 * @author Kingen
 * @see PornTubeStar#getVideos()
 * @since 2021/3/17
 */
public interface PornTubeVideoIndex extends IntIdentifier, TitleSupplier, CoverSupplier {

}
