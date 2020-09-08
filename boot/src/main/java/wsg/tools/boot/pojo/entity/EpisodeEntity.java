package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Episode entity.
 *
 * @author Kingen
 * @since 2020/8/7
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("3")
public class EpisodeEntity extends SubjectEntity {
    private Long seasonId;
    private Integer currentEpisode;
}
