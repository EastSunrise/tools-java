package wsg.tools.internet.resource.entity.resource;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.UnknownResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;
import wsg.tools.internet.resource.entity.resource.valid.*;

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
        try {
            url = Thunder.decodeThunder(url.strip(), charset);
        } catch (IllegalArgumentException e) {
            throw new InvalidResourceException(e.getMessage(), title, url, password);
        }

        if (StringUtils.containsIgnoreCase(url, Ed2kResource.SCHEME)) {
            return Ed2kResource.of(title, url);
        }
        if (StringUtils.containsIgnoreCase(url, MagnetResource.SCHEME)) {
            return MagnetResource.of(title, url);
        }
        if (StringUtils.containsIgnoreCase(url, YyetsResource.SCHEME)) {
            return YyetsResource.of(title, url);
        }
        if (StringUtils.containsIgnoreCase(url, BaiduDiskResource.HOST)) {
            return BaiduDiskResource.of(title, url, password);
        }
        if (StringUtils.containsIgnoreCase(url, UcDiskResource.HOST)) {
            return UcDiskResource.of(title, url);
        }
        if (StringUtils.containsAny(url, HttpResource.PERMIT_SCHEMES)) {
            return HttpResource.of(title, url);
        }
        throw new UnknownResourceException(title, url, password);
    }
}
