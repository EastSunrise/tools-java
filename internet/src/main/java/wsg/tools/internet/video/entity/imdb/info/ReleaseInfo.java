package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbInfo;
import wsg.tools.internet.video.enums.RegionEnum;

import java.time.LocalDate;

/**
 * Info of a released date.
 * <p>
 * Attributes: festival releases, city specific releases, limited releases,
 * premiere releases, non-theatrical releases, re-releases, language regions in Switzerland, etc.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class ReleaseInfo extends BaseImdbInfo {

    private RegionEnum region;
    private LocalDate date;
}
