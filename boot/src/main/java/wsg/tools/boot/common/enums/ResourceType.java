package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.impl.*;

/**
 * Types of resources.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public enum ResourceType implements AkaPredicate<Class<? extends Resource>>, TextSupplier {

    /**
     * all types, referring to subclasses of {@link Resource}.
     */
    ED2K(Ed2kResource.class, "ed2k"),
    MAGNET(MagnetResource.class, "magnet"),
    YYETS(YyetsResource.class, "yyets"),
    BAIDU_DISK(BaiduDiskResource.class, "Baidu Yun Disk"),
    UC_DISK(UcDiskResource.class, "UC Yun DIsk"),
    HTTP(HttpResource.class, "HTTP/HTTPS/FTP");

    private final Class<? extends Resource> clazz;
    private final String text;

    ResourceType(Class<? extends Resource> clazz, String text) {
        this.clazz = clazz;
        this.text = text;
    }

    @Override
    public boolean alsoKnownAs(Class<? extends Resource> other) {
        return clazz.equals(other);
    }

    @Override
    public String getText() {
        return text;
    }
}
