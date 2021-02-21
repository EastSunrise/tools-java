package wsg.tools.boot.pojo.entity.subject;

import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.resource.item.intf.YearSupplier;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.douban.DoubanIdentifier;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Duration;
import java.util.List;

/**
 * Movie entity.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Entity
@Table(name = "video_movie")
public class MovieEntity extends IdentityEntity implements DoubanIdentifier, ImdbIdentifier, YearSupplier {

    @Column(unique = true)
    private Long dbId;

    @Column(unique = true, length = 10)
    private String imdbId;

    @Column(length = 63)
    private String zhTitle;

    @Column(length = 127)
    private String enTitle;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 63)
    private List<LanguageEnum> languages;

    @Column(nullable = false, length = 63)
    private List<Duration> durations;

    public String getZhTitle() {
        return zhTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public List<LanguageEnum> getLanguages() {
        return languages;
    }

    public List<Duration> getDurations() {
        return durations;
    }

    public String getEnTitle() {
        return enTitle;
    }

    @Override
    public Integer getYear() {
        return year;
    }

    @Override
    public Long getDbId() {
        return dbId;
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }
}
