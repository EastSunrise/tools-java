package wsg.tools.boot.pojo.entity.subject;

import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.Duration;
import java.util.List;

/**
 * Episode entity.
 *
 * @author Kingen
 * @since 2020/8/7
 */
@Setter
@Entity
@Table(name = "video_episode")
public class EpisodeEntity extends IdentityEntity {

    @Column(nullable = false, unique = true, length = 10)
    private String imdbId;

    @Column(nullable = false, length = 127)
    private String enTitle;

    private Integer year;

    @Column(length = 63)
    private List<Duration> durations;

    @Column(nullable = false)
    private Long seasonId;

    @Column(nullable = false)
    private Integer currentEpisode;

    public List<Duration> getDurations() {
        return durations;
    }

    public Long getSeasonId() {
        return seasonId;
    }

    public Integer getCurrentEpisode() {
        return currentEpisode;
    }

    public String getEnTitle() {
        return enTitle;
    }

    public Integer getYear() {
        return year;
    }

    public String getImdbId() {
        return imdbId;
    }
}
