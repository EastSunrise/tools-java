package wsg.tools.internet.resource.entity.rrys.common;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.resource.site.RrysSite;

/**
 * Formats of files of {@link RrysSite}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public enum FormatEnum implements TextSupplier {
    APP("APP"),
    MP4("MP4"),
    WEB_DL("WEB-DL"),
    HDTV("HDTV"),
    HR_HDTV("HR-HDTV"),
    HD("720P"),
    HD_SUB("HDSUB"),
    HD_WEB("WEB-720P"),
    HD_BD("BD-720P"),
    FHD("1080P"),
    FHD_WEB("WEB-1080P"),
    FHD_BD("BD-1080P"),
    FOUR_K("4K-2160P"),
    THREE_D("3D"),
    DVD("DVD"),
    DVD_SCR("DVDSCR"),
    RMVB("RMVB"),
    OST("OST"),
    UNDEFINED("undefined"),
    TRAILER("预告片"),
    GAME("游戏"),
    ;

    private final String text;

    FormatEnum(String text) {this.text = text;}

    public static FormatEnum of(String text) {
        if (StringUtils.isBlank(text)) {
            return null;
        }
        if ("MP4MP4MP4MP4MP4".equals(text)) {
            return FormatEnum.MP4;
        }
        return EnumUtilExt.deserializeText(text, FormatEnum.class, true);
    }

    @Override
    public String getText() {
        return text;
    }
}
