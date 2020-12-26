package wsg.tools.internet.resource.entity.rrys.common;

import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.resource.site.RrysSite;

/**
 * Ways to download files of {@link RrysSite}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public enum FileWayEnum implements CodeSupplier<Integer>, TitleSupplier {

    ED2K(1, "电驴"),
    MAGNET(2, "磁力"),
    DISK(9, "网盘"),
    CT_FILE(12, "诚通网盘"),
    BaiduYun(102, "百度云"),
    ACFUN(103, "AcFun"),
    BILIBILI(104, "Bilibili"),
    Youku(105, "优酷"),
    SOHU(106, "搜狐"),
    LE(107, "乐视"),
    TENCENT(108, "腾讯"),
    FANTASY(114, "范特西视频"),
    WeiYun(115, "微云"),
    ;

    private final int code;
    private final String title;

    FileWayEnum(int code, String title) {
        this.code = code;
        this.title = title;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
