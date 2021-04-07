package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import java.util.Objects;

/**
 * A simple video on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findPageByCategory(int, PornTubePageReq)
 * @see PornTubeSite#findAllByTag(PornTubeTag)
 * @since 2021/3/31
 */
public class PornTubeSimpleVideo extends PornTubeVideoIndex {

    private final Duration duration;
    private final int views;
    private final int likes;

    PornTubeSimpleVideo(int id, URL thumb, String title, Duration duration, int views, int likes) {
        super(id, thumb, title);
        this.duration = Objects.requireNonNull(duration);
        this.views = views;
        this.likes = likes;
    }

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
