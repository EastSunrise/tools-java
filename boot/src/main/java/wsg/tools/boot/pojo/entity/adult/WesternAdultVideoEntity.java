package wsg.tools.boot.pojo.entity.adult;

import java.time.Duration;
import javax.annotation.Nonnull;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import wsg.tools.boot.config.MinioStored;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.common.io.Filetype;

/**
 * The entity of a western adult video.
 *
 * @author Kingen
 * @since 2021/4/10
 */
@Entity
@Table(
    name = "west_adult_video",
    uniqueConstraints = @UniqueConstraint(name = "uni_wt_adult_video_source", columnNames = {
        "sname", "subtype", "rid"
    })
)
public class WesternAdultVideoEntity extends IdentityEntity {

    private static final long serialVersionUID = -7759493928773926950L;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 127)
    @MinioStored(type = Filetype.IMAGE)
    private String cover;

    @Column(length = 127)
    @MinioStored(type = Filetype.VIDEO)
    private String preview;

    private Duration duration;

    @Column(length = 127)
    @MinioStored(type = Filetype.VIDEO)
    private String video;

    @Column(length = 1023)
    private String[] tags;

    private String[] categories;

    @Column(length = 2047)
    private String description;

    @Embedded
    private Source source;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Nonnull
    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Nonnull
    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }
}
