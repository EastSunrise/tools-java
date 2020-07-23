package wsg.tools.internet.video.enums;

import lombok.Getter;
import wsg.tools.common.jackson.intf.CodeSupplier;
import wsg.tools.common.jackson.intf.TextSupplier;
import wsg.tools.common.jackson.intf.TitleSupplier;

/**
 * Enum for cities.
 *
 * @author Kingen
 * @since 2020/7/17
 */
public enum CityEnum implements CodeSupplier<String>, TextSupplier, TitleSupplier {
    /**
     * Cities
     */
    BEIJING(1000, "PEK", "Beijing", "北京"),
    SHANGHAI(2900, "SHA", "Shanghai", "上海"),
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
}
