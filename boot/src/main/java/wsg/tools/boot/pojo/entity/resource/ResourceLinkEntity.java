package wsg.tools.boot.pojo.entity.resource;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entities of links of resources.
 *
 * @author Kingen
 * @see ValidResource
 * @since 2020/10/30
 */
@Getter
@Setter
@Entity
@Table(name = "resource_link")
public class ResourceLinkEntity extends IdentityEntity {

    @Column(nullable = false, length = 63)
    private String itemUrl;

    @Column(length = 511)
    private String title;

    @Column(nullable = false)
    private ResourceType type;

    @Column(nullable = false, length = 4095)
    private String url;

    @Column
    private String filename;

    private Long length;
}
