package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;

import javax.persistence.*;

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
public class SeasonEntity extends SubjectEntity implements DoubanIdentifier {

    @Column(nullable = false, unique = true)
    private Long dbId;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false)
    private Integer currentSeason;

    @Column(nullable = false)
    private Integer episodesCount;

    @ManyToOne
    @JoinColumn(name = "series_id", nullable = false)
    private SeriesEntity series;
}
