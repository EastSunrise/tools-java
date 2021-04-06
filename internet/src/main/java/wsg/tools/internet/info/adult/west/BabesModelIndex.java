package wsg.tools.internet.info.adult.west;

import java.net.URL;
import wsg.tools.internet.base.PathSupplier;
import wsg.tools.internet.movie.common.CoverSupplier;

/**
 * An index pointing to a model in the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findModelPage(BabesModelPageReq)
 * @see BabesModelPageResult
 * @since 2021/3/15
 */
public class BabesModelIndex implements CoverSupplier, PathSupplier {

    /**
     * Name as path
     */
    private final String namePath;
    private final String name;
    private final URL cover;
    private final int videos;
    private final int photos;

    BabesModelIndex(String namePath, String name, URL cover, int videos, int photos) {
        this.namePath = namePath;
        this.name = name;
        this.cover = cover;
        this.videos = videos;
        this.photos = photos;
    }

    @Override
    public String getAsPath() {
        return namePath;
    }

    public String getName() {
        return name;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    public int getVideos() {
        return videos;
    }

    public int getPhotos() {
        return photos;
    }
}
