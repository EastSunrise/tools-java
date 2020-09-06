package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbInfo;
import wsg.tools.internet.video.enums.RegionEnum;

/**
 * Info of a laboratory.
 * <p>
 * Format: Laboratory Name, Location, Region.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class Laboratory extends BaseImdbInfo {

    private String name;
    private String location;
    private RegionEnum region;
}
