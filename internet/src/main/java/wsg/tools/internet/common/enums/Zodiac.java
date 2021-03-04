package wsg.tools.internet.common.enums;

import wsg.tools.common.util.function.TitleSupplier;

/**
 * Twelve zodiacs.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum Zodiac implements TitleSupplier {

    RAT("鼠"),
    OX("牛"),
    TIGER("虎"),
    RABBIT("兔"),
    DRAGON("龙"),
    SNAKE("蛇"),
    HORSE("马"),
    SHEEP("羊"),
    MONKEY("猴"),
    ROOSTER("鸡"),
    DOG("狗"),
    PIG("猪");

    private final String title;

    Zodiac(String title) {this.title = title;}

    @Override
    public String getTitle() {
        return title;
    }
}
