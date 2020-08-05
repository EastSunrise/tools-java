package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Entity of TV series.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Setter
@Getter
@Entity
@DiscriminatorValue("1")
public class SeriesEntity extends SubjectEntity {
    private Integer seasonsCount;
}
