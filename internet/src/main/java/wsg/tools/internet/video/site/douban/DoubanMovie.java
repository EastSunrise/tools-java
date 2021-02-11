package wsg.tools.internet.video.site.douban;

import javax.annotation.Nullable;
import java.time.Duration;
import java.util.List;

/**
 * Movie.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class DoubanMovie extends BaseDoubanSubject {

    private List<Duration> durations;

    DoubanMovie() {
    }

    @Nullable
    public List<Duration> getDurations() {
        return durations;
    }

    void setDurations(List<Duration> durations) {
        this.durations = durations;
    }
}