package wsg.tools.internet.info.adult.west;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.CoverSupplier;

/**
 * An index pointing to a video on the site.
 *
 * @author Kingen
 * @see PornTubeStar#getVideos()
 * @since 2021/3/17
 */
public interface PornTubeVideoIndex extends IntIdentifier, TitleSupplier, CoverSupplier {

}
