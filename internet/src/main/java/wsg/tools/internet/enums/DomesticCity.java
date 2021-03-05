package wsg.tools.internet.enums;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Enum for domestic cities.
 *
 * @author Kingen
 * @since 2020/7/17
 */
public enum DomesticCity implements IntCodeSupplier, TextSupplier, TitleSupplier {
    /**
     * domestic cities
     */
    PEK(1101, "Beijing", "北京"),
    SHA(3101, "Shanghai", "上海"),
    NKG(3201, "Nanjing", "南京"),
    ;

    private final int code;
    private final String text;
    private final String title;

    DomesticCity(int code, String text, String title) {
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
}
