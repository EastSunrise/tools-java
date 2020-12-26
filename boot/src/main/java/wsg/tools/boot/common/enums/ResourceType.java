package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;
import wsg.tools.internet.resource.entity.resource.valid.*;

/**
 * Types of resources.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public enum ResourceType implements AkaPredicate<Class<? extends ValidResource>>, TextSupplier {

    /**
     * all types, referring to subclasses of {@link ValidResource}.
     */
    ED2K(Ed2kResource.class, "ed2k"),
    MAGNET(MagnetResource.class, "magnet"),
    BAIDU_DISK(BaiduDiskResource.class, "Baidu Yun Disk"),
    UC_DISK(UcDiskResource.class, "UC Yun DIsk"),
    HTTP(HttpResource.class, "HTTP/HTTPS/FTP");

    private final Class<? extends ValidResource> clazz;
    private final String text;

    ResourceType(Class<? extends ValidResource> clazz, String text) {
        this.clazz = clazz;
        this.text = text;
    }

    @Override
    public boolean alsoKnownAs(Class<? extends ValidResource> other) {
        return clazz.equals(other);
    }

    @Override
    public String getText() {
        return text;
    }
}
