package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import java.util.Objects;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.TitledAdultEntry;

/**
 * A simple video on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findPageByCategory(int, PornTubePageReq)
 * @see PornTubeSite#findAllByTag(PornTubeTag)
 * @since 2021/3/31
 */
public class PornTubeSimpleVideo implements PornTubeVideoIndex, TitledAdultEntry, DurationSupplier {

    private final int id;
    private final URL thumb;
    private final String title;
    private Duration duration;
    private int views;
    private int likes;

    PornTubeSimpleVideo(int id, URL thumb, String title) {
        this.id = id;
        this.thumb = thumb;
        this.title = title;
    }

    PornTubeSimpleVideo(int id, URL thumb, String title, Duration duration, int views, int likes) {
        this.id = id;
        this.thumb = Objects.requireNonNull(thumb);
        this.title = AssertUtils.requireNotBlank(title);
        this.duration = Objects.requireNonNull(duration);
        this.views = views;
        this.likes = likes;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public URL getCoverURL() {
        return thumb;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public int getViews() {
        return views;
    }

    public int getLikes() {
        return likes;
    }
}
