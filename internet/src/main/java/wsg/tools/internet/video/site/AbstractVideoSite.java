package wsg.tools.internet.video.site;

import com.fasterxml.jackson.databind.ObjectMapper;
import wsg.tools.internet.base.BaseSite;

/**
 * Abstract class for basis of video sites
 *
 * @author Kingen
 * @since 2020/6/20
 */
public abstract class AbstractVideoSite extends BaseSite {

    public AbstractVideoSite(String name, String domain) {
        super(name, domain);
    }

    public AbstractVideoSite(String name, String domain, double permitsPerSecond) {
        super(name, domain, permitsPerSecond);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        return super.getObjectMapper();
    }
}
