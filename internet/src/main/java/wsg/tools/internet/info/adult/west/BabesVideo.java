package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.common.VideoQuality;

/**
 * A video with details on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findById(String)
 * @since 2021/4/3
 */
public class BabesVideo implements BabesVideoIndex, WesternAdultEntry, UpdateDatetimeSupplier {

    private final int id;
    private final String titlePath;
    private final String title;
    private final URL cover;
    private final Duration duration;
    private final double rating;
    private final int views;
    private URL preview;
    private VideoQuality quality;
    private int likes;
    private int dislikes;
    private int comments;
    private String description;
    private BabesMember author;
    private LocalDateTime uploadTime;
    private URL video;
    private String[] tags;

    BabesVideo(int id, String titlePath, String title, URL cover, Duration duration,
        double rating, int views) {
        this.id = id;
        this.titlePath = titlePath;
        this.title = title;
        this.cover = cover;
        this.duration = duration;
        this.rating = rating;
        this.views = views;
    }

    BabesVideo(int id, String titlePath, String title, URL cover, Duration duration, double rating,
        int views, int likes, int dislikes, int comments, String description, BabesMember author,
        LocalDateTime uploadTime) {
        this.id = id;
        this.titlePath = titlePath;
        this.title = title;
        this.cover = cover;
        this.duration = duration;
        this.rating = rating;
        this.views = views;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
        this.description = description;
        this.author = author;
        this.uploadTime = uploadTime;
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
    public URL getCoverURL() {
        return cover;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    @Override
    public int getViews() {
        return views;
    }

    @Override
    public double getRating() {
        return rating;
    }

    @Override
    public URL getPreviewURL() {
        return preview;
    }

    void setPreview(URL preview) {
        this.preview = preview;
    }

    @Override
    public VideoQuality getQuality() {
        return quality;
    }

    void setQuality(VideoQuality quality) {
        this.quality = quality;
    }

    public int getLikes() {
        return likes;
    }

    public int getDislikes() {
        return dislikes;
    }

    public int getComments() {
        return comments;
    }

    @Override
    public String getDescription() {
        return description;
    }

    public BabesMember getAuthor() {
        return author;
    }

    @Override
    public LocalDateTime getUpdate() {
        return uploadTime;
    }

    @Override
    public URL getVideoURL() {
        return video;
    }

    void setVideo(URL video) {
        this.video = video;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }
}
