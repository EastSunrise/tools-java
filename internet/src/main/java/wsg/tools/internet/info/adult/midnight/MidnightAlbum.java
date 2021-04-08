package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import wsg.tools.internet.info.adult.view.AlbumSupplier;

/**
 * An item with a list of images.
 *
 * @author Kingen
 * @see MidnightSite#findAlbum(int)
 * @since 2021/3/2
 */
public class MidnightAlbum extends BaseMidnightItem implements AlbumSupplier {

    private final List<URL> album;

    MidnightAlbum(int id, String title, LocalDateTime addTime, List<URL> album) {
        super(id, title, addTime);
        this.album = Collections.unmodifiableList(album);
    }

    @Override
    public List<URL> getAlbum() {
        return album;
    }
}
