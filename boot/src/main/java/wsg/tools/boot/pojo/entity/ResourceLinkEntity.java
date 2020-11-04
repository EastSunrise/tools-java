package wsg.tools.boot.pojo.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.pojo.base.IdentityEntity;
import wsg.tools.boot.pojo.enums.ResourceType;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Entities of links of resources.
 *
 * @author Kingen
 * @see BaseValidResource
 * @since 2020/10/30
 */
@Getter
@Setter
@Entity
@Table(name = "resource_link")
public class ResourceLinkEntity extends IdentityEntity {

    @Column(nullable = false)
    private Long itemId;

    @Column(length = 511)
    private String title;

    @Column(nullable = false)
    private ResourceType type;

    @Column(nullable = false, length = 4095)
    private String url;
}
