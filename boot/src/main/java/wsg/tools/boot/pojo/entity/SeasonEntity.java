package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity of one season of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Getter
@Entity
@DiscriminatorValue("2")
public class SeasonEntity extends SubjectEntity {
    private Integer currentSeason;
    private Integer episodesCount;
    private Long seriesId;
}
