package wsg.tools.internet.info.adult.west;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.CoverSupplier;
import wsg.tools.internet.base.view.IntIdentifier;

/**
 * An index pointing to a video on the site.
 *
 * @author Kingen
 * @see PornTubeStar#getVideos()
 * @since 2021/3/17
 */
public interface PornTubeVideoIndex extends IntIdentifier, TitleSupplier, CoverSupplier {

}
