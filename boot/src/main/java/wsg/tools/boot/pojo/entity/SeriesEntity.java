package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.IdentityEntity;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.persistence.*;
import java.time.Duration;
import java.time.Year;
import java.util.List;

/**
 * Entity of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Getter
@Entity
@Table(name = "video_series")
public class SeriesEntity extends IdentityEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String imdbId;

    @Column(nullable = false, length = 63)
    private String title;

    @Column(nullable = false, length = 127)
    private String text;

    @Column(nullable = false)
    private Year year;

    @Column(nullable = false, length = 63)
    private List<LanguageEnum> languages;

    @Column(nullable = false, length = 63)
    private List<Duration> durations;

    @Column(nullable = false)
    private Integer seasonsCount;

    @OneToMany(mappedBy = "seriesId", fetch = FetchType.EAGER)
    private List<SeasonEntity> seasons;
}
