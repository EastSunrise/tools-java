package wsg.tools.internet.info.adult.view;

import java.net.URL;
import java.util.List;

/**
 * Represents a supplier of an album that contains a series of images.
 *
 * @author Kingen
 * @since 2021/4/7
 */
public interface AlbumSupplier {

    /**
     * Returns the album of the entity.
     *
     * @return the list of images as the album
     */
    List<URL> getAlbum();
}
