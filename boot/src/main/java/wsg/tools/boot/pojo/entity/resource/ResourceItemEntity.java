package wsg.tools.boot.pojo.entity.resource;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;
import wsg.tools.internet.resource.common.YearSupplier;
import wsg.tools.internet.resource.movie.BasicItem;

/**
 * Items of resources.
 *
 * @author Kingen
 * @see BasicItem
 * @since 2020/11/3
 */
@Getter
@Setter
@Entity
@Table(
    name = "resource_item",
    indexes = {
        @Index(name = "index_imdb", columnList = "imdbId"),
        @Index(name = "index_resource_item_domain", columnList = Source.DOMAIN_COLUMN)
    }
)
public class ResourceItemEntity extends IdentityEntity
    implements YearSupplier, DoubanIdentifier, ImdbIdentifier {

    private static final long serialVersionUID = -6437618032369837427L;

    @Embedded
    private Source source;

    @Column(nullable = false)
    private String title;

    private Integer year;

    private Long dbId;

    @Column(length = 10)
    private String imdbId;

    @Column(length = 0)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    private Boolean identified;

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
