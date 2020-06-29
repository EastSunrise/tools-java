package wsg.tools.internet.video.enums;

import wsg.tools.common.util.AssertUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Enum for countries.
 *
 * @author Kingen
 * @since 2020/6/26
 */
public final class Country extends AbstractLocale<Country> {

    private static final Set<Country> COUNTRIES;

    /*
      Initialize ISO and extended countries.
     */
    static {
        COUNTRIES = new HashSet<>(256);
        COUNTRIES.add(of("CN", new String[]{"中国大陆"}));
        COUNTRIES.add(of("HK", new String[]{"Hong Kong", "中国香港"}));
        COUNTRIES.add(of("TW", new String[]{"中国台湾"}));
        COUNTRIES.add(of("DE", new String[]{"西德", "West Germany"}));
        COUNTRIES.add(of("US", new String[]{"USA"}));
        COUNTRIES.add(of("GB", new String[]{"UK"}));
        COUNTRIES.add(of("CZ", new String[]{"Czech Republic"}));
        Arrays.stream(Locale.getISOCountries()).forEach(c -> COUNTRIES.add(of(c, null)));
    }

    protected Country(String code, String text, String title, String[] aka) {
        super(code, text, title, aka);
    }

    /**
     * Obtains an instance of country from code.
     */
    public static Country ofCode(String code) {
        return AssertUtils.findOne(COUNTRIES.stream(), l -> l.code.equals(code),
                "Unknown country code %s", code);
    }

    /**
     * Obtains an instance of country from English text.
     */
    public static Country ofText(String text) {
        return AssertUtils.findOne(COUNTRIES.stream(), c -> c.text.equals(text) || c.alsoKnownAs(text),
                "Unknown country text %s", text);
    }

    /**
     * Obtains an instance of country from Chinese title
     */
    public static Country ofTitle(String title) {
        return AssertUtils.findOne(COUNTRIES.stream(), c -> c.title.equals(title) || c.alsoKnownAs(title),
                "Unknown country title %s", title);
    }

    private static Country of(String code, String[] aka) {
        Locale locale = new Locale("", code);
        return new Country(locale.getCountry(), locale.getDisplayCountry(Locale.ENGLISH),
                locale.getDisplayCountry(Locale.CHINESE), aka);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj instanceof Country) {
            Country other = (Country) obj;
            return code.equals(other.code);
        }
        return false;
    }
}
