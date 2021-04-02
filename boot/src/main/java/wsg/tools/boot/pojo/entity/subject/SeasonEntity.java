package wsg.tools.boot.pojo.entity.subject;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.movie.common.YearSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;

/**
 * Entity of one season of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Entity
@Table(
    name = "video_season",
    indexes = @Index(name = "unique_season_db", columnList = "dbId", unique = true)
)
public class SeasonEntity extends IdentityEntity implements DoubanIdentifier, YearSupplier {

    private static final long serialVersionUID = 4265823069538348592L;

    @Column(nullable = false, unique = true)
    private Long dbId;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false, length = 63)
    private String zhTitle;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 63)
    private List<Language> languages;

    @Column(nullable = false, length = 63)
    private List<Duration> durations;

    @Column(nullable = false)
    private Integer currentSeason;

    @Column(nullable = false)
    private Integer episodesCount;

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false)
    private SeriesEntity series;

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

    @Override
    public Integer getYear() {
        return year;
    }

    @Override
    public Long getDbId() {
        return dbId;
    }

    public Integer getCurrentSeason() {
        return currentSeason;
    }

    public Integer getEpisodesCount() {
        return episodesCount;
    }

    public SeriesEntity getSeries() {
        return series;
    }
}
