package wsg.tools.internet.info.adult;

import java.net.URL;
import java.time.Duration;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.IntIdentifier;
import wsg.tools.internet.info.adult.common.VideoQuality;

/**
 * An index pointing to a video on {@link BabesTubeSite}.
 *
 * @author Kingen
 * @since 2021/3/16
 */
public class BabesVideoIndex implements IntIdentifier {

    private final int id;
    private final String path;
    private final String title;
    private URL cover;
    private URL preview;
    private VideoQuality quality;
    private Duration duration;
    private String author;
    private double rating;
    private int views;

    public BabesVideoIndex(int id, String path, String title) {
        this.id = id;
        this.path = AssertUtils.requireNotBlank(path, "path of a video");
        this.title = AssertUtils.requireNotBlank(title, "title of a video");
    }

    @Override
    public int getId() {
        return id;
    }

    public String getPath() {
        return path;
    }

    public String getTitle() {
        return title;
    }

    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = cover;
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

    void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getAuthor() {
        return author;
    }

    void setAuthor(String author) {
        this.author = author;
    }

    public double getRating() {
        return rating;
    }

    void setRating(double rating) {
        this.rating = rating;
    }

    public int getViews() {
        return views;
    }

    void setViews(int views) {
        this.views = views;
    }
}
