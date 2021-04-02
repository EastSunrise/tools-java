package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.support.BaiduDiskLink;
import wsg.tools.internet.download.support.Ed2kLink;
import wsg.tools.internet.download.support.HttpLink;
import wsg.tools.internet.download.support.MagnetLink;
import wsg.tools.internet.download.support.ThunderDiskLink;
import wsg.tools.internet.download.support.UcDiskLink;
import wsg.tools.internet.download.support.YyetsLink;

/**
 * Types of resources.
 *
 * @author Kingen
 * @since 2020/11/3
 */
public enum ResourceType implements IntCodeSupplier, AkaPredicate<Class<? extends Link>>,
    TextSupplier {

    /**
     * @see Ed2kLink
     */
    ED2K(11, Ed2kLink.class, "ed2k"),
    /**
     * @see MagnetLink
     */
    MAGNET(12, MagnetLink.class, "magnet"),
    /**
     * @see HttpLink
     */
    HTTP(13, HttpLink.class, "HTTP/HTTPS/FTP"),
    /**
     * @see BaiduDiskLink
     */
    BAIDU_DISK(21, BaiduDiskLink.class, "Baidu Yun Disk"),
    /**
     * @see UcDiskLink
     */
    UC_DISK(22, UcDiskLink.class, "UC Yun Disk"),
    /**
     * @see ThunderDiskLink
     */
    THUNDER_DISK(23, ThunderDiskLink.class, "Thunder Disk"),
    /**
     * @see YyetsLink
     */
    YYETS(31, YyetsLink.class, "yyets"),
    ;

    private final int code;
    private final Class<? extends Link> clazz;
    private final String text;

    ResourceType(int code, Class<? extends Link> clazz, String text) {
        this.code = code;
        this.clazz = clazz;
        this.text = text;
    }

    @Override
    public boolean alsoKnownAs(Class<? extends Link> other) {
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
