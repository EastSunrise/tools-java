package wsg.tools.internet.resource.site.impl;

import lombok.extern.slf4j.Slf4j;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.resource.site.intf.ResourceRepository;

/**
 * Base class of sites of resources of video.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Slf4j
abstract class AbstractResourceSite<T, ID> extends BaseSite implements ResourceRepository<T, ID> {

    protected AbstractResourceSite(String name, String host) {
        super(name, host);
    }

    protected AbstractResourceSite(String name, String domain, double postPermitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, postPermitsPerSecond);
    }

    protected AbstractResourceSite(String name, SchemeEnum scheme, String domain, double postPermitsPerSecond) {
        super(name, scheme, domain, 10D, postPermitsPerSecond);
    }
}
