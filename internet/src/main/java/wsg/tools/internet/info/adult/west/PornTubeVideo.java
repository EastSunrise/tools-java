package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.UpdateDatetimeSupplier;

/**
 * A video on the site.
 *
 * @author Kingen
 * @see PornTubeSite#findAllVideoIndices()
 * @see PornTubeSite#findById(Integer)
 * @since 2021/3/17
 */
public class PornTubeVideo extends PornTubeSimpleVideo implements UpdateDatetimeSupplier {

    private final URL video;
    private final String description;
    private final URL source;
    private final LocalDateTime postTime;
    private Map<Integer, String> categories;
    private List<PornTubeTag> tags;

    PornTubeVideo(int id, URL thumb, String title, Duration duration, int views, int likes,
        URL video, String description, URL source, LocalDateTime postTime) {
        super(id, thumb, title, duration, views, likes);
        this.video = Objects.requireNonNull(video);
        this.description = AssertUtils.requireNotBlank(description);
        this.source = Objects.requireNonNull(source);
        this.postTime = postTime;
    }

    public URL getVideo() {
        return video;
    }

    public String getDescription() {
        return description;
    }

    public URL getSource() {
        return source;
    }

    public Map<Integer, String> getCategories() {
        return categories;
    }

    void setCategories(Map<Integer, String> categories) {
        this.categories = categories;
    }

    public List<PornTubeTag> getTags() {
        return tags;
    }

    void setTags(List<PornTubeTag> tags) {
        this.tags = tags;
    }

    @Override
    public LocalDateTime lastUpdate() {
        return postTime;
    }
}
