package wsg.tools.boot.pojo.entity.resource;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.resource.item.BaseItem;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateTimeSupplier;
import wsg.tools.internet.resource.item.intf.YearSupplier;
import wsg.tools.internet.video.site.douban.DoubanIdentifier;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Items of resources.
 *
 * @author Kingen
 * @see BaseItem
 * @since 2020/11/3
 */
@Getter
@Setter
@Entity
@Table(name = "resource_item", uniqueConstraints = {
        @UniqueConstraint(name = "uk_site_key", columnNames = {"site", "sid"})
}, indexes = {
        @Index(name = "index_imdb", columnList = "imdbId")
})
public class ResourceItemEntity extends IdentityEntity implements TypeSupplier, YearSupplier, DoubanIdentifier, ImdbIdentifier, UpdateTimeSupplier<LocalDateTime> {

    @Column(nullable = false, length = 15)
    private String site;

    @Column(nullable = false)
    private Integer sid;

    @Column(nullable = false, length = 63, unique = true)
    private String url;

    @Column(nullable = false)
    private String title;

    private VideoType type;

    private Integer year;

    private Long dbId;

    @Column(length = 10)
    private String imdbId;

    @Column(length = 0)
    private LocalDateTime updateTime;

    @Column(nullable = false)
    private Boolean identified;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
