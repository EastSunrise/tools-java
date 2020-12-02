package wsg.tools.internet.resource.entity.resource;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.resource.base.InvalidResource;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.base.UnknownResource;
import wsg.tools.internet.resource.entity.resource.valid.Ed2kResource;
import wsg.tools.internet.resource.entity.resource.valid.HttpResource;
import wsg.tools.internet.resource.entity.resource.valid.MagnetResource;

import java.nio.charset.Charset;

/**
 * Factory of resources.
 *
 * @author Kingen
 * @since 2020/10/8
 */
@Slf4j
public final class ResourceFactory {

    public static Resource create(String title, @NonNull String url) {
        return create(title, url, Constants.UTF_8);
    }

    /**
     * Create a resource based on the given url and title.
     */
    public static Resource create(String title, @NonNull String url, Charset charset) {
        try {
            url = Thunder.decodeThunder(url.strip(), charset);
        } catch (IllegalArgumentException e) {
            return new InvalidResource(title, url, e.getMessage());
        }

        try {
            if (StringUtils.startsWithAny(url, Ed2kResource.SCHEME)) {
                return Ed2kResource.of(title, url);
            }
            if (StringUtils.startsWithAny(url, MagnetResource.SCHEME)) {
                return MagnetResource.of(title, url);
            }
            if (StringUtils.startsWithAny(url, HttpResource.PERMIT_SCHEMES)) {
                return HttpResource.of(title, url);
            }
            return new UnknownResource(title, url);
        } catch (IllegalArgumentException e) {
            return new InvalidResource(title, url, e.getMessage());
        }
    }
}
