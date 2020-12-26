package wsg.tools.internet.resource.entity.rrys.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.RegionEnum;

/**
 * An item of a resource.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class ResourceItem {

    private Long id;
    private String cnname;
    private String enname;
    private RegionEnum area;
}
