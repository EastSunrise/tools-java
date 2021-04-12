package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.util.List;
import java.util.Locale;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.CoverSupplier;

/**
 * A star on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findStarByName(String)
 * @since 2021/3/17
 */
public class PornTubeStar implements PathSupplier, CoverSupplier {

    private final String name;
    private final URL cover;
    private final List<PornTubeVideoIndex> videos;

    PornTubeStar(String name, URL cover, List<PornTubeVideoIndex> videos) {
        this.name = name;
        this.cover = cover;
        this.videos = videos;
    }

    public String getName() {
        return name;
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    public List<PornTubeVideoIndex> getVideos() {
        return videos;
    }

    @Override
    public String getAsPath() {
        return name.replace(" ", "-").toLowerCase(Locale.ROOT);
    }
}
