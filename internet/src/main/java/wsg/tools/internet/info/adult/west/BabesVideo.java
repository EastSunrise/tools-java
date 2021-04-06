package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * A video with details on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findById(String)
 * @since 2021/4/3
 */
public class BabesVideo extends BabesVideoIndex {

    private final int likes;
    private final int dislikes;
    private final int comments;
    private final String description;
    private final BabesMember author;
    private final LocalDateTime uploadTime;
    private URL source;
    private String[] tags;

    BabesVideo(int id, String titlePath, String title, URL cover, Duration duration, double rating,
        int views, int likes, int dislikes, int comments, String description, BabesMember author,
        LocalDateTime uploadTime) {
        super(id, titlePath, title, cover, duration, rating, views);
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
        this.description = description;
        this.author = author;
        this.uploadTime = uploadTime;
    }

    @Override
    public URL getPreview() {
        return null;
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

    public String getDescription() {
        return description;
    }

    public BabesMember getAuthor() {
        return author;
    }

    public LocalDateTime getUploadTime() {
        return uploadTime;
    }

    public URL getSource() {
        return source;
    }

    void setSource(URL source) {
        this.source = source;
    }

    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }
}
