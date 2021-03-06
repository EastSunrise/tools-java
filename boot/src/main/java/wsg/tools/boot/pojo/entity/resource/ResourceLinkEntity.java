package wsg.tools.boot.pojo.entity.resource;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Index;
import javax.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.pojo.entity.base.IdentityEntity;

/**
 * Entities of links of resources.
 *
 * @author Kingen
 * @see wsg.tools.internet.download.Link
 * @since 2020/10/30
 */
@Getter
@Setter
@Entity
@Table(
    name = "resource_link",
    indexes = @Index(name = "index_resource_link_item_id", columnList = "itemId")
)
public class ResourceLinkEntity extends IdentityEntity {

    private static final long serialVersionUID = 868731424501072343L;

    @Column(nullable = false)
    private Long itemId;

    @Column(length = 511)
    private String title;

    @Column(nullable = false)
    private ResourceType type;

    @Column(nullable = false, length = 16383)
    private String url;

    @Column(length = 4)
    private String password;

    @Column
    private String filename;

    private Long length;
}
