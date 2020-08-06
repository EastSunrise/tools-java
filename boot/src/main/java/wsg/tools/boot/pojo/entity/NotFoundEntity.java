package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Entity for not-found ids.
 *
 * @author Kingen
 * @since 2020/8/6
 */
@Getter
@Setter
@Entity
@Table(name = "video_not_found")
public class NotFoundEntity extends BaseEntity {
    @Id
    private String id;
}
