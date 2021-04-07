package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.util.Objects;
import wsg.tools.common.lang.AssertUtils;

/**
 * An index pointing to a video on the site.
 *
 * @author Kingen
 * @see PornTubeStar#getVideos()
 * @since 2021/3/17
 */
public class PornTubeVideoIndex {

    private final int id;
    private final URL thumb;
    private final String title;

    PornTubeVideoIndex(int id, URL thumb, String title) {
        this.id = id;
        this.thumb = Objects.requireNonNull(thumb);
        this.title = AssertUtils.requireNotBlank(title);
    }

    public int getId() {
        return id;
    }

    public URL getThumb() {
        return thumb;
    }

    public String getTitle() {
        return title;
    }
}
