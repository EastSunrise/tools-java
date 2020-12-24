package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.enums.LanguageEnum;

import javax.persistence.*;
import java.time.Duration;
import java.time.Year;
import java.util.List;

/**
 * Entity of one season of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Getter
@Entity
@Table(name = "video_season")
public class SeasonEntity extends IdentityEntity implements DoubanIdentifier {

    @Column(nullable = false, unique = true)
    private Long dbId;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false, length = 63)
    private String title;

    @Column(nullable = false)
    private Year year;

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
}
