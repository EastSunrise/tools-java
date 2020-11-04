package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.IdentityEntity;
import wsg.tools.internet.resource.common.VideoType;

import javax.persistence.*;
import java.util.List;

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
public class ResourceItemEntity extends IdentityEntity {

    @Column(unique = true, nullable = false, length = 63)
    private String url;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private VideoType videoType;

    private Integer year;

    private Long dbId;

    @Column(length = 10)
    private String imdbId;

    @OneToMany(mappedBy = "itemId", fetch = FetchType.EAGER)
    private List<ResourceLinkEntity> links;
}
