package wsg.tools.boot.pojo.enums;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;
import wsg.tools.internet.resource.entity.resource.valid.*;

/**
 * Types of resources.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public enum ResourceType implements AkaPredicate<Class<? extends BaseValidResource>> {

    /**
     * all types, referring to subclasses of {@link BaseValidResource}.
     */
    ED2K(Ed2kResource.class),
    MAGNET(MagnetResource.class),
    BAIDU_DISK(PanResource.class),
    UC_DISK(YunResource.class),
    HTTP(HttpResource.class);

    private final Class<? extends BaseValidResource> clazz;

    ResourceType(Class<? extends BaseValidResource> clazz) {this.clazz = clazz;}

    @Override
    public boolean alsoKnownAs(Class<? extends BaseValidResource> other) {
        return clazz.equals(other);
    }
}
