package wsg.tools.internet.info.adult.midnight;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * An album in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class MidnightAlbum extends BaseMidnightItem {

    private List<String> images;

    MidnightAlbum(int id, String title, LocalDateTime release) {
        super(id, title, release);
    }

    public List<String> getImages() {
        return Collections.unmodifiableList(images);
    }

    void setImages(List<String> images) {
        this.images = Collections.unmodifiableList(images);
    }
}
