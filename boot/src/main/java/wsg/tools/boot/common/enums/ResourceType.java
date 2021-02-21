package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.impl.*;

/**
 * Types of resources.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public enum ResourceType implements IntCodeSupplier, AkaPredicate<Class<? extends AbstractResource>>, TextSupplier {

    /**
     * all types, referring to subclasses of {@link AbstractResource}.
     */
    ED2K(11, Ed2kResource.class, "ed2k"),
    MAGNET(12, MagnetResource.class, "magnet"),
    HTTP(13, HttpResource.class, "HTTP/HTTPS/FTP"),
    BAIDU_DISK(21, BaiduDiskResource.class, "Baidu Yun Disk"),
    UC_DISK(22, UcDiskResource.class, "UC Yun Disk"),
    THUNDER_DISK(23, ThunderDiskResource.class, "Thunder Disk"),
    YYETS(31, YyetsResource.class, "yyets"),
    ;

    private final int code;
    private final Class<? extends AbstractResource> clazz;
    private final String text;

    ResourceType(int code, Class<? extends AbstractResource> clazz, String text) {
        this.code = code;
        this.clazz = clazz;
        this.text = text;
    }

    @Override
    public boolean alsoKnownAs(Class<? extends AbstractResource> other) {
        return clazz.equals(other);
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
