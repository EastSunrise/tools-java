package wsg.tools.boot.pojo.entity.subject;

import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.movie.common.enums.LanguageEnum;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.resource.common.YearSupplier;

import javax.persistence.*;
import java.time.Duration;
import java.util.List;

/**
 * Entity of one season of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Entity
@Table(name = "video_season")
public class SeasonEntity extends IdentityEntity implements DoubanIdentifier, YearSupplier {

    @Column(nullable = false, unique = true)
    private Long dbId;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false, length = 63)
    private String zhTitle;

    @Column(nullable = false)
    private Integer year;

    @Column(length = 63)
    private List<LanguageEnum> languages;

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

    public List<LanguageEnum> getLanguages() {
        return languages;
    }

    public List<Duration> getDurations() {
        return durations;
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
