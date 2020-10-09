package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
public class SeasonEntity extends SubjectEntity {

    @Column(nullable = false, unique = true)
    private Long dbId;

    @Column(length = 127)
    private String originalTitle;

    @Column(nullable = false)
    private Long seriesId;

    @Column(nullable = false)
    private Integer currentSeason;

    @Column(nullable = false)
    private Integer episodesCount;

    @OneToMany(mappedBy = "seasonId", fetch = FetchType.EAGER)
    private List<EpisodeEntity> episodes;
}
