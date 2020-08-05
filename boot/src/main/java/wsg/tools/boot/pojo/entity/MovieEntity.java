package wsg.tools.boot.pojo.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

/**
 * Movie entity.
 *
 * @author Kingen
 * @since 2020/8/5
 */
@Entity
@DiscriminatorValue("0")
public class MovieEntity extends SubjectEntity {
}
