package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseEntity;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Relation between ids.
 *
 * @author Kingen
 * @since 2020/9/22
 */
@Getter
@Setter
@Entity
@Table(name = "video_id_relation")
public class IdRelationEntity extends BaseEntity {

    @Id
    private String imdbId;
    private long dbId;
}
