package wsg.tools.internet.info.adult;

import java.net.URL;
import java.util.Objects;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.Identifier;
import wsg.tools.internet.resource.common.CoverSupplier;

/**
 * An index pointing to a model in the {@link BabesTubeSite}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class BabesModelIndex implements Identifier<String>, CoverSupplier {

    private final String id;
    private final String name;
    private final URL cover;
    private final int videos;
    private final int photos;

    BabesModelIndex(String id, String name, URL cover, int videos, int photos) {
        this.id = AssertUtils.requireNotBlank(id, "id of a model");
        this.name = AssertUtils.requireNotBlank(name, "name of a model");
        this.cover = Objects.requireNonNull(cover, "cover of a model");
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
