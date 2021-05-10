package wsg.tools.boot.dao.api.adapter.impl;

import java.net.URL;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.WestAdultEntryAdapter;
import wsg.tools.internet.info.adult.west.PornTubeVideo;

/**
 * Adapter that accepts a {@link PornTubeVideo} and implements {@link WestAdultEntryAdapter}.
 *
 * @author Kingen
 * @since 2021/4/18
 */
public class PornTubeVideoAdapter implements WestAdultEntryAdapter {

    private final PornTubeVideo video;

    public PornTubeVideoAdapter(PornTubeVideo video) {
        this.video = video;
    }

    @Override
    public String getTitle() {
        return video.getTitle();
    }

    @Override
    public URL getCoverURL() {
        return video.getCoverURL();
    }

    @Override
    public URL getVideoURL() {
        return video.getVideoURL();
    }

    @Override
    public Duration getDuration() {
        return video.getDuration();
    }

    @Override
    public String getDescription() {
        return video.getDescription();
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return video.getTags();
    }

    @Nonnull
    @Override
    public Set<String> getCategories() {
        return video.getCategories();
    }

    @Nonnull
    @Override
    public List<URL> getImages() {
        return new ArrayList<>();
    }
}
