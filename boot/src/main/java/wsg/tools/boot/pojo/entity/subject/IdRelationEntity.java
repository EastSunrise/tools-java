package wsg.tools.boot.pojo.entity.subject;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.BaseEntity;
import wsg.tools.internet.video.site.douban.DoubanIdentifier;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.persistence.Column;
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
public class IdRelationEntity extends BaseEntity implements DoubanIdentifier, ImdbIdentifier {

    @Id
    @Column(length = 10)
    private String imdbId;

    @Column(nullable = false)
    private Long dbId;
}
