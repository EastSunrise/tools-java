package wsg.tools.internet.resource.entity.resource;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.resource.base.InvalidResource;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.valid.Ed2kResource;
import wsg.tools.internet.resource.entity.resource.valid.HttpResource;
import wsg.tools.internet.resource.entity.resource.valid.MagnetResource;

import java.util.function.Supplier;

/**
 * Factory of resources.
 *
 * @author Kingen
 * @since 2020/10/8
 */
@Slf4j
public final class ResourceFactory {

    /**
     * Create a resource based on the given url and title.
     *
     * @param supplier if failed to create a resource with the given arguments
     */
    public static Resource create(String title, @NonNull String url, Supplier<Resource> supplier) {
        try {
            url = Thunder.decodeThunder(url.strip());
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
        } catch (IllegalArgumentException e) {
            return new InvalidResource(title, url, e.getMessage());
        }
        return supplier.get();
    }
}
