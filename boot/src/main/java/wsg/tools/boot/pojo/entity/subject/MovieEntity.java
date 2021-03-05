package wsg.tools.boot.pojo.entity.subject;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;
import wsg.tools.internet.resource.common.YearSupplier;

/**
 * Movie entity.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Entity
@Table(name = "video_movie")
public class MovieEntity extends IdentityEntity implements DoubanIdentifier, ImdbIdentifier,
    YearSupplier {

    private static final long serialVersionUID = 7109265333600711325L;

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
    private List<Language> languages;

    @Column(nullable = false, length = 63)
    private List<Duration> durations;

    public String getZhTitle() {
        return zhTitle;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public List<Language> getLanguages() {
        return Collections.unmodifiableList(languages);
    }

    public List<Duration> getDurations() {
        return Collections.unmodifiableList(durations);
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
