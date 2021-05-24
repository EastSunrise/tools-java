package wsg.tools.internet.common.enums;

import wsg.tools.common.util.function.AliasSupplier;

/**
 * Twelve constellations.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public enum Constellation implements AliasSupplier {
    /**
     * Twelve constellations
     */
    ARIES("白羊座", "牡羊座", "牧羊座"),
    TAURUS("金牛座", "おうし座"),
    GEMINI("双子座"),
    CANCER("巨蟹座", "かに座"),
    LEO("狮子座"),
    VIRGO("处女座", "處女座"),
    LIBRA("天秤座", "天平座", "天枰座", "てんびん座"),
    SCORPIO("天蝎座", "天蠍座"),
    SAGITTARIUS("射手座", "人马座", "いて座"),
    CAPRICORN("摩羯座", "山羊座", "魔羯座", "魔蝎座"),
    AQUARIUS("水瓶座", "みずがめ座"), PISCES("双鱼座");

    private final String zhName;
    private final String[] alias;

    Constellation(String zhName, String... alias) {
        this.zhName = zhName;
        this.alias = alias;
    }

    public String getZhName() {
        return zhName;
    }

    @Override
    public String[] getAlias() {
        return alias;
    }
}
