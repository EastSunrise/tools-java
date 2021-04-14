package wsg.tools.internet.info.adult.view;

import java.net.URL;

/**
 * Represents a supplier of an image of the entry.
 *
 * @author Kingen
 * @since 2021/4/13
 */
public interface ImageSupplier {

    /**
     * Returns the url of the image of the entry.
     *
     * @return the url of the image
     */
    URL getImageURL();
}
