package wsg.tools.internet.video.site.adult.midnight;

import wsg.tools.common.util.function.TitleSupplier;

import java.util.List;

/**
 * An album in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class MidnightAlbum implements TitleSupplier {

    private final String title;
    private List<String> images;

    MidnightAlbum(String title) {
        this.title = title;
    }

    public List<String> getImages() {
        return images;
    }

    void setImages(List<String> images) {
        this.images = images;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
