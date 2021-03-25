package wsg.tools.internet.info.adult.midnight;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import lombok.Getter;
import wsg.tools.common.lang.AssertUtils;

/**
 * An item with an album.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class MidnightAlbum extends BaseMidnightEntry {

    @Getter
    private final List<URL> images;

    MidnightAlbum(int id, String title, LocalDateTime release, List<URL> images) {
        super(id, title, release);
        AssertUtils.requireNotEmpty(images, "images of the album");
        this.images = Collections.unmodifiableList(images);
    }
}
