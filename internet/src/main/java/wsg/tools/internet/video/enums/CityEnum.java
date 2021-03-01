package wsg.tools.internet.video.enums;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.common.PathParameterized;

/**
 * Enum for cities.
 *
 * @author Kingen
 * @since 2020/7/17
 */
public enum CityEnum implements IntCodeSupplier, TextSupplier, TitleSupplier, PathParameterized {
    /**
     * Cities
     */
    PEK(1101, "Beijing", "北京"),
    SHA(3101, "Shanghai", "上海"),
    NKG(3201, "Nanjing", "南京"),
    ;

    private final int code;
    private final String text;
    private final String title;

    CityEnum(int code, String text, String title) {
        this.code = code;
        this.text = text;
        this.title = title;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPath() {
        return text.toLowerCase();
    }
}
