package wsg.tools.internet.common.enums;

import wsg.tools.common.util.function.AliasSupplier;

/**
 * Twelve zodiacs.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum Zodiac implements AliasSupplier {

    /**
     * Twelve zodiacs
     */
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

    private final String zhName;

    Zodiac(String zhName) {
        this.zhName = zhName;
    }

    public String getZhName() {
        return zhName;
    }

    @Override
    public String[] getAlias() {
        return new String[]{zhName};
    }
}
