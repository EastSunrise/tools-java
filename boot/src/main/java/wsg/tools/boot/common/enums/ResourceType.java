package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.*;

/**
 * Types of resources.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public enum ResourceType implements IntCodeSupplier, AkaPredicate<Class<? extends AbstractLink>>, TextSupplier {

    /**
     * all types, referring to subclasses of {@link AbstractLink}.
     */
    ED2K(11, Ed2kLink.class, "ed2k"),
    MAGNET(12, MagnetLink.class, "magnet"),
    HTTP(13, HttpLink.class, "HTTP/HTTPS/FTP"),
    BAIDU_DISK(21, BaiduDiskLink.class, "Baidu Yun Disk"),
    UC_DISK(22, UcDiskLink.class, "UC Yun Disk"),
    THUNDER_DISK(23, ThunderDiskLink.class, "Thunder Disk"),
    YYETS(31, YyetsLink.class, "yyets"),
    ;

    private final int code;
    private final Class<? extends AbstractLink> clazz;
    private final String text;

    ResourceType(int code, Class<? extends AbstractLink> clazz, String text) {
        this.code = code;
        this.clazz = clazz;
        this.text = text;
    }

    @Override
    public boolean alsoKnownAs(Class<? extends AbstractLink> other) {
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
