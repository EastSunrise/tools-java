package wsg.tools.internet.common.enums;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Twelve constellations.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public enum Constellation implements TitleSupplier, AkaPredicate<String> {
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

    private final String title;
    private final String[] aka;

    Constellation(String title, String... aka) {
        this.title = title;
        this.aka = aka;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        return title.equals(other) || ArrayUtils.contains(aka, other);
    }
}
