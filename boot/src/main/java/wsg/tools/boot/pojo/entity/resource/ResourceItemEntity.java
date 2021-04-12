package wsg.tools.boot.pojo.entity.resource;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.config.MinioStored;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.common.io.Filetype;
import wsg.tools.internet.movie.common.ResourceState;
import wsg.tools.internet.movie.common.YearSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;

/**
 * Items of resources.
 *
 * @author Kingen
 * @see wsg.tools.internet.movie.resource.view.IdentifierItem
 * @since 2020/11/3
 */
@Getter
@Setter
@Entity
@Table(
    name = "resource_item",
    indexes = {
        @Index(name = "index_resource_item_imdb", columnList = "imdbId"),
        @Index(name = "index_resource_item_db", columnList = "dbId")
    },
    uniqueConstraints = @UniqueConstraint(
        name = "unique_resource_item_source",
        columnNames = {"domain", "rid"}
    )
)
public class ResourceItemEntity extends IdentityEntity
    implements YearSupplier, DoubanIdentifier, ImdbIdentifier {

    private static final long serialVersionUID = -6437618032369837427L;

    @Embedded
    private Source source;

    @Column(nullable = false)
    private String title;

    @Column(length = 127)
    @MinioStored(type = Filetype.IMAGE)
    private String cover;

    private Integer year;

    @Column(length = 15)
    private ResourceState state;

    private Long dbId;

    @Column(length = 10)
    private String imdbId;

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
