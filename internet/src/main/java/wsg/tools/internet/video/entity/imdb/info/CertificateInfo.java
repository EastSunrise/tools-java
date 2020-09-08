package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbInfo;
import wsg.tools.internet.video.enums.RegionEnum;

/**
 * Info of certificate (ratings).
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class CertificateInfo extends BaseImdbInfo {

    private RegionEnum region;
    private String certificate;
}