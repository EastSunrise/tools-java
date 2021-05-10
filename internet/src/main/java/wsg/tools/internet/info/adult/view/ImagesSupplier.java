package wsg.tools.internet.info.adult.view;

import java.net.URL;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of an album that contains a series of images.
 *
 * @author Kingen
 * @since 2021/4/7
 */
@EntityProperty
public interface ImagesSupplier {

    /**
     * Returns the images of the entity.
     *
     * @return the list of the urls of the images
     */
    @Nonnull
    List<URL> getImages();
}
