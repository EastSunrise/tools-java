package wsg.tools.internet.video.enums;

import lombok.Getter;
import wsg.tools.common.function.CodeSupplier;
import wsg.tools.common.function.TextSupplier;
import wsg.tools.common.function.TitleSupplier;
import wsg.tools.internet.base.PathParameterized;

/**
 * Enum for cities.
 *
 * @author Kingen
 * @since 2020/7/17
 */
public enum CityEnum implements CodeSupplier<String>, TextSupplier, TitleSupplier, PathParameterized {
    /**
     * Cities
     */
    BEIJING(1101, "PEK", "Beijing", "北京"),
    SHANGHAI(3101, "SHA", "Shanghai", "上海"),
    NANJING(3201, "NKG", "Nanjing", "南京"),
    ;

    @Getter
    private final int no;
    private final String code;
    private final String text;
    private final String title;

    CityEnum(int no, String code, String text, String title) {
        this.no = no;
        this.code = code;
        this.text = text;
        this.title = title;
    }

    @Override
    public String getCode() {
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
