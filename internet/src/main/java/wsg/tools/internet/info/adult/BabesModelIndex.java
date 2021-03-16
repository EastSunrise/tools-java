package wsg.tools.internet.info.adult;

import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.Identifier;

/**
 * An index pointing to a model in the {@link BabesTubeSite}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class BabesModelIndex implements Identifier<String> {

    private final String id;
    private final String name;
    private final String cover;
    private final int videos;
    private final int photos;

    BabesModelIndex(String id, String name, String cover, int videos, int photos) {
        this.id = id;
        this.name = AssertUtils.requireNotBlank(name, "name of a model");
        this.cover = AssertUtils.requireNotBlank(cover, "cover of a model");
        this.videos = videos;
        this.photos = photos;
    }

    @Override
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCover() {
        return cover;
    }

    public int getVideos() {
        return videos;
    }

    public int getPhotos() {
        return photos;
    }
}
