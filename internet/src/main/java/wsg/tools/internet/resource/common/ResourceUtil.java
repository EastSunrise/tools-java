package wsg.tools.internet.resource.common;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.resource.*;

/**
 * Utility for operating resources.
 *
 * @author Kingen
 * @since 2020/10/8
 */
@Slf4j
public final class ResourceUtil {

    private static final String PAN_HOST = "pan.baidu.com";

    /**
     * Classify a url.
     *
     * @param url source url
     * @return target resource of the given url
     */
    public static AbstractResource classifyUrl(@NonNull String url) {
        url = Thunder.decodeThunder(url);
        try {
            if (StringUtils.startsWithAny(url, HttpResource.PERMIT_SCHEMES)) {
                if (url.contains(PAN_HOST)) {
                    return new PanResource(url);
                }
                return new HttpResource(url);
            }
            if (StringUtils.startsWithAny(url, Ed2kResource.SCHEME)) {
                return new Ed2kResource(url);
            }
            if (StringUtils.startsWithAny(url, MagnetResource.SCHEME)) {
                return new MagnetResource(url);
            }
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
        }
        return new InvalidResource(url);
    }
}
