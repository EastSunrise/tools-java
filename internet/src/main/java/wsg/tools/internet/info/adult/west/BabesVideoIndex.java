package wsg.tools.internet.info.adult.west;

import java.io.Serializable;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.info.adult.common.VideoQuality;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.ImagesSupplier;

/**
 * An index pointing to a video on {@link BabesTubeSite}.
 *
 * @author Kingen
 * @see BabesTubeSite#findPage(BabesPageReq)
 * @see BabesTubeSite#findPageByCategory(String, BabesPageReq)
 * @see BabesPageResult
 * @since 2021/3/16
 */
public class BabesVideoIndex implements IntIdentifier,
    TitleSupplier, PathSupplier, DurationSupplier, ImagesSupplier, Serializable {

    private static final long serialVersionUID = 4535102252625207385L;
    private static final String SCREENSHOT_FORMAT =
        "https://temw6juvcn.ent-cdn.com/contents/videos_screenshots/%d/%d/400x225/%d.jpg";
    private static final int SEPARATE_ID = 51494;

    private final int id;
    private final String titlePath;
    private final String title;
    private final Duration duration;
    private final double rating;
    private final int views;
    private VideoQuality quality;

    BabesVideoIndex(int id, String titlePath, String title, Duration duration, double rating,
        int views) {
        this.id = id;
        this.titlePath = titlePath;
        this.title = title;
        this.duration = duration;
        this.rating = rating;
        this.views = views;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getAsPath() {
        return id + "/" + titlePath;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    public int getViews() {
        return views;
    }

    public VideoQuality getQuality() {
        return quality;
    }

    void setQuality(VideoQuality quality) {
        this.quality = quality;
    }

    @Nonnull
    @Override
    public List<URL> getImages() {
        int count = id < SEPARATE_ID ? 30 : 10;
        return IntStream.rangeClosed(1, count).mapToObj(
            i -> NetUtils.createURL(String.format(SCREENSHOT_FORMAT, id / 1000 * 1000, id, i)))
            .collect(Collectors.toList());
    }
}
