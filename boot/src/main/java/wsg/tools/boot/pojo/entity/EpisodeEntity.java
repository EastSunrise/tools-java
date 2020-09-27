package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.IdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

/**
 * Episode entity.
 *
 * @author Kingen
 * @since 2020/8/7
 */
@Getter
@Setter
@Entity
@Table(name = "video_episode")
public class EpisodeEntity extends IdentityEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String imdbId;

    @Column(nullable = false, length = 127)
    private String text;

    private LocalDate released;

    @Column(length = 63)
    private List<Duration> durations;

    @Column(nullable = false)
    private Long seasonId;

    @Column(nullable = false)
    private Integer currentEpisode;
}
