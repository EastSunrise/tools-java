package wsg.tools.boot.common.enums;

import wsg.tools.common.util.function.IntCodeSupplier;
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
public enum ResourceType implements IntCodeSupplier {

    /**
     * @see Ed2kLink
     */
    ED2K(11, Ed2kLink.class),
    /**
     * @see MagnetLink
     */
    MAGNET(12, MagnetLink.class),
    /**
     * @see HttpLink
     */
    HTTP(13, HttpLink.class),
    /**
     * @see BaiduDiskLink
     */
    BAIDU_DISK(21, BaiduDiskLink.class),
    /**
     * @see UcDiskLink
     */
    UC_DISK(22, UcDiskLink.class),
    /**
     * @see ThunderDiskLink
     */
    THUNDER_DISK(23, ThunderDiskLink.class),
    /**
     * @see YyetsLink
     */
    YYETS(31, YyetsLink.class),
    ;

    private final int code;
    private final Class<? extends Link> linkClass;

    ResourceType(int code, Class<? extends Link> linkClass) {
        this.code = code;
        this.linkClass = linkClass;
    }

    @Override
    public int getCode() {
        return code;
    }

    public Class<? extends Link> getLinkClass() {
        return linkClass;
    }
}
