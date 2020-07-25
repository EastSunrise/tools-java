package wsg.tools.internet.video.site;

import wsg.tools.internet.base.BaseSite;

/**
 * Abstract class for basis of video sites
 *
 * @author Kingen
 * @since 2020/6/20
 */
public abstract class BaseVideoSite extends BaseSite {

    public BaseVideoSite(String name, String domain) {
        super(name, domain);
    }

    public BaseVideoSite(String name, String domain, double permitsPerSecond) {
        super(name, domain, permitsPerSecond);
    }
}
