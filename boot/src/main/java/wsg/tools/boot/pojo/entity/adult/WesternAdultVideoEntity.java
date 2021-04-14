package wsg.tools.boot.pojo.entity.adult;

import java.net.URL;
import java.time.Duration;
import java.util.Optional;
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
import wsg.tools.common.net.NetUtils;
import wsg.tools.internet.info.adult.view.Classified;
import wsg.tools.internet.info.adult.view.PreviewSupplier;
import wsg.tools.internet.info.adult.west.WesternAdultEntry;

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
        "domain", "subtype", "rid"
    })
)
public class WesternAdultVideoEntity extends IdentityEntity
    implements WesternAdultEntry, Classified, PreviewSupplier {

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

    private String[] tags;

    private String[] categories;

    @Column(length = 63)
    private String description;

    @Embedded
    private Source source;

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public URL getCoverURL() {
        return Optional.ofNullable(cover).map(NetUtils::createURL).orElse(null);
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    @Override
    public URL getPreviewURL() {
        return Optional.ofNullable(preview).map(NetUtils::createURL).orElse(null);
    }

    public String getPreview() {
        return preview;
    }

    public void setPreview(String preview) {
        this.preview = preview;
    }

    @Override
    public Duration getDuration() {
        return duration;
    }

    public void setDuration(Duration duration) {
        this.duration = duration;
    }

    @Override
    public URL getVideoURL() {
        return Optional.ofNullable(video).map(NetUtils::createURL).orElse(null);
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    @Nonnull
    @Override
    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    @Nonnull
    @Override
    public String[] getCategories() {
        return categories;
    }

    public void setCategories(String[] categories) {
        this.categories = categories;
    }

    @Override
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
