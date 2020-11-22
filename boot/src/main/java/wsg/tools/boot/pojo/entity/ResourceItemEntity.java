package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.BaseEntity;
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Objects;

/**
 * Items of resources.
 *
 * @author Kingen
 * @see wsg.tools.internet.resource.entity.item.BaseItem
 * @since 2020/11/3
 */
@Getter
@Setter
@Entity
@Table(name = "resource_item")
public class ResourceItemEntity extends BaseEntity implements DoubanIdentifier, ImdbIdentifier {

    @Id
    @Column(length = 63)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private VideoType videoType;

    private Integer year;

    private Long dbId;

    @Column(length = 10)
    private String imdbId;

    @Column(nullable = false)
    private Boolean identified;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ResourceItemEntity that = (ResourceItemEntity) o;
        return Objects.equals(url, that.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(url);
    }
}
