package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import wsg.tools.internet.info.adult.view.ImagesSupplier;

/**
 * An item with a list of images.
 *
 * @author Kingen
 * @see MidnightSite#findAlbum(int)
 * @since 2021/3/2
 */
public class MidnightAlbum extends BaseMidnightItem implements ImagesSupplier {

    private final List<URL> images;

    MidnightAlbum(int id, String title, LocalDateTime addTime, List<URL> images) {
        super(id, title, addTime);
        this.images = Collections.unmodifiableList(images);
    }

    @Nonnull
    @Override
    public List<URL> getImages() {
        return images;
    }
}
