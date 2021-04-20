package wsg.tools.boot.dao.api.adapter.impl;

import java.net.URL;
import java.time.Duration;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.WesternAdultEntry;
import wsg.tools.internet.info.adult.west.PornTubeVideo;

/**
 * Adapts a {@link PornTubeVideo} to a {@link WesternAdultEntry}.
 *
 * @author Kingen
 * @since 2021/4/18
 */
public class PornTubeVideoAdapter implements WesternAdultEntry {

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
    public URL getPreviewURL() {
        return null;
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
    public String[] getTags() {
        return video.getTags();
    }

    @Nonnull
    @Override
    public String[] getCategories() {
        return video.getCategories();
    }
}
