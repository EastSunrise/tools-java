package wsg.tools.internet.resource.entity.resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.UnknownResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;
import wsg.tools.internet.resource.entity.resource.valid.*;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Objects;

/**
 * Factory of resources.
 *
 * @author Kingen
 * @since 2020/10/8
 */
@Slf4j
public final class ResourceFactory {

    public static ValidResource create(String title, String url, String password) throws InvalidResourceException {
        return create(title, url, password, Constants.UTF_8);
    }

    /**
     * Create a resource based on the given url and title.
     */
    public static ValidResource create(String title, String url, String password, Charset charset) throws InvalidResourceException {
        Objects.requireNonNull(url);
        if (StringUtils.startsWithIgnoreCase(url, Thunder.PREFIX)) {
            try {
                url = Thunder.decodeThunder(url, charset);
            } catch (IllegalArgumentException e) {
                throw new InvalidResourceException("Not a valid thunder url", title, url, password);
            }
        }
        try {
            url = URLDecoder.decode(url, charset);
        } catch (IllegalArgumentException ignored) {
        }

        if (StringUtils.startsWithIgnoreCase(url, Ed2kResource.ED2K_PREFIX)) {
            return Ed2kResource.of(title, url);
        }
        if (StringUtils.startsWithIgnoreCase(url, MagnetResource.MAGNET_PREFIX)) {
            return MagnetResource.of(title, url);
        }
        if (StringUtils.startsWithIgnoreCase(url, YyetsResource.YYETS_PREFIX)) {
            return YyetsResource.of(title, url);
        }
        if (StringUtils.containsIgnoreCase(url, BaiduDiskResource.BAIDU_DISK_HOST)) {
            throw new InvalidResourceException("password", title, url, password);
        }
        if (StringUtils.containsIgnoreCase(url, UcDiskResource.UC_DISK_HOST)) {
            return UcDiskResource.of(title, url);
        }
        for (String prefix : HttpResource.HTTP_PREFIXES) {
            if (StringUtils.startsWithIgnoreCase(url, prefix)) {
                return HttpResource.of(title, url);
            }
        }
        throw new UnknownResourceException("Unknown type of resource", title, url, password);
    }
}
