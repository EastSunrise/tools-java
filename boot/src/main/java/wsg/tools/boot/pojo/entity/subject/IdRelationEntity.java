package wsg.tools.boot.pojo.entity.subject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.BaseEntity;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * Relation between ids.
 *
 * @author Kingen
 * @since 2020/9/22
 */
@Setter
@Entity
@Table(name = "video_id_relation")
public class IdRelationEntity extends BaseEntity implements DoubanIdentifier, ImdbIdentifier {

    private static final long serialVersionUID = 2918051901304370785L;

    @Id
    @Column(length = 10)
    private String imdbId;

    @Column(nullable = false)
    private Long dbId;

    @Override
    public Long getDbId() {
        return dbId;
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }
}
