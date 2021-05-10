package wsg.tools.internet.info.adult.view;

import java.net.URL;
import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of the url of the video source.
 *
 * @author Kingen
 * @since 2021/4/10
 */
@EntityProperty
public interface VideoSupplier {

    /**
     * Returns the url of the video source.
     *
     * @return the url of the video
     */
    URL getVideoURL();
}
