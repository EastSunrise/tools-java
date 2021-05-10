package wsg.tools.internet.info.adult.west;

import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * A video with details on the site.
 *
 * @author Kingen
 * @see BabesTubeSite#findById(String)
 * @since 2021/4/3
 */
public class BabesVideo extends BabesVideoIndex
    implements UpdateDatetimeSupplier, CoverSupplier, Tagged {

    private static final long serialVersionUID = 6543234874741944438L;

    private final URL cover;
    private final int likes;
    private final int dislikes;
    private final int comments;
    private final String description;
    private final BabesMember author;
    private final LocalDateTime uploadTime;
    private Set<String> tags = new HashSet<>();

    BabesVideo(int id, String titlePath, String title, URL cover, Duration duration, double rating,
        int views, int likes, int dislikes, int comments, String description, BabesMember author,
        LocalDateTime uploadTime) {
        super(id, titlePath, title, duration, rating, views);
        this.cover = cover;
        this.likes = likes;
        this.dislikes = dislikes;
        this.comments = comments;
        this.description = description;
        this.author = author;
        this.uploadTime = uploadTime;
    }

    @Override
    public URL getCoverURL() {
        return cover;
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

    @Override
    public LocalDateTime getUpdate() {
        return uploadTime;
    }

    @Override
    @Nonnull
    public Set<String> getTags() {
        return tags;
    }

    void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
