package wsg.tools.boot.dao.api.adapter.impl;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import javax.annotation.Nonnull;
import wsg.tools.boot.dao.api.adapter.WesternAdultEntry;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.west.BabesVideo;
import wsg.tools.internet.info.adult.west.BabesVideoIndex;

/**
 * Adapts a {@link BabesVideo} to a {@link WesternAdultEntry}.
 *
 * @author Kingen
 * @since 2021/4/18
 */
public class BabesVideoAdapter implements IntIdentifier, WesternAdultEntry, UpdateDatetimeSupplier {

    private final BabesVideoIndex index;
    private final BabesVideo video;

    public BabesVideoAdapter(@Nonnull BabesVideoIndex index, @Nonnull BabesVideo video) {
        this.index = index;
        this.video = video;
    }

    @Override
    public int getId() {
        return index.getId();
    }

    @Override
    public LocalDateTime getUpdate() {
        return video.getUpdate();
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
        return new String[0];
    }

    @Override
    public URL getVideoURL() {
        return null;
    }
}
