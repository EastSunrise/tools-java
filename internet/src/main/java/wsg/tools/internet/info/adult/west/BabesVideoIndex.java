package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import wsg.tools.internet.base.IntIdentifier;
import wsg.tools.internet.base.PathSupplier;
import wsg.tools.internet.info.adult.common.VideoQuality;

/**
 * An index pointing to a video on {@link BabesTubeSite}.
 *
 * @author Kingen
 * @see BabesTubeSite#findPage(BabesPageReq)
 * @see BabesTubeSite#findPageByCategory(String, BabesPageReq)
 * @see BabesPageResult
 * @since 2021/3/16
 */
public class BabesVideoIndex implements IntIdentifier, PathSupplier {

    private final int id;
    private final String titlePath;
    private final String title;
    private final URL cover;
    private final Duration duration;
    private final double rating;
    private final int views;
    private URL preview;
    private VideoQuality quality;

    BabesVideoIndex(int id, String titlePath, String title, URL cover, Duration duration,
        double rating, int views) {
        this.id = id;
        this.titlePath = titlePath;
        this.title = title;
        this.cover = cover;
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

    public String getTitle() {
        return title;
    }

    public URL getCover() {
        return cover;
    }

    public URL getPreview() {
        return preview;
    }

    void setPreview(URL preview) {
        this.preview = preview;
    }

    public VideoQuality getQuality() {
        return quality;
    }

    void setQuality(VideoQuality quality) {
        this.quality = quality;
    }

    public Duration getDuration() {
        return duration;
    }

    public double getRating() {
        return rating;
    }

    public int getViews() {
        return views;
    }
}
