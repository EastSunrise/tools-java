package wsg.tools.internet.info.adult.view;

import java.net.URL;

/**
 * Represents a supplier of the url of the video source.
 *
 * @author Kingen
 * @since 2021/4/10
 */
public interface VideoSupplier {

    /**
     * Returns the url of the video source.
     *
     * @return the url of the video
     */
    URL getVideoURL();
}
