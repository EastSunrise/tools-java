package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.site.imdb.pojo.base.BaseImdbInfo;

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
