package wsg.tools.internet.info.adult.view;

import java.net.URL;
import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of an image of the entity.
 *
 * @author Kingen
 * @since 2021/4/13
 */
@EntityProperty
public interface ImageSupplier {

    /**
     * Returns the url of the image of the entity.
     *
     * @return the url of the image
     */
    URL getImageURL();
}
