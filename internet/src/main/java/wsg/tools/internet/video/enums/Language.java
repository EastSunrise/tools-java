package wsg.tools.internet.video.enums;

import wsg.tools.common.util.AssertUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Enum for languages.
 *
 * @author Kingen
 * @since 2020/6/26
 */
public final class Language extends AbstractLocale<Language> {

    private static final Set<Language> LANGUAGES;

    /*
     Initialize ISO and extended languages
     */
    static {
        LANGUAGES = new HashSet<>(256);
        // manual languages, three-letter abbreviation as code
        LANGUAGES.add(new Language("sin", "Sindarin", "辛达林语"));
        LANGUAGES.add(new Language("que", "Quenya", "昆雅语"));
        LANGUAGES.add(new Language("sil", "Sign Languages", "手语"));
        LANGUAGES.add(new Language("asl", "American Sign Language", "美国手语"));
        LANGUAGES.add(new Language("ksl", "Korean Sign Language", "韩国手语"));
        LANGUAGES.add(new Language("dot", "Dothraki", "多斯拉克语"));
        LANGUAGES.add(new Language("kli", "Klingon", "克林贡语"));
        LANGUAGES.add(new Language("cre", "Creole", "克里奥尔语"));
        LANGUAGES.add(new Language("paw", "Pawnee", "波尼语"));
        LANGUAGES.add(new Language("nai", "North American Indian", "北美印第安语"));
        LANGUAGES.add(new Language("hun", "Huns", "匈奴语"));
        // known languages
        LANGUAGES.add(of("yue", null));
        LANGUAGES.add(of("fil", null));
        LANGUAGES.add(of("haw", null));
        LANGUAGES.add(of("eu", new String[]{"Basque"}));
        LANGUAGES.add(of("iw", new String[]{"希伯来语"}));
        LANGUAGES.add(of("gd", new String[]{"Scots"}));
        LANGUAGES.add(of("hi", new String[]{"北印度语"}));
        LANGUAGES.add(of("kl", new String[]{"Greenlandic"}));
        LANGUAGES.add(of("ug", new String[]{"Uighur"}));
        LANGUAGES.add(of("in", new String[]{"印度尼西亚语", "印尼语"}));
        LANGUAGES.add(of("el", new String[]{"Greek, Ancient (to 1453)", "Ancient (to 1453)", "古希腊语"}));
        LANGUAGES.add(of("en", new String[]{"Old English", "古英语", "古代英语"}));
        LANGUAGES.add(of("ko", new String[]{"釜山方言"}));
        LANGUAGES.add(of("zh", new String[]{"汉语普通话", "普通话", "Mandarin",
                "贵州独山话", "湖南方言", "云南方言", "安徽方言", "陕西方言", "河南方言", "甘肃方言", "贵州方言",
                "北京话", "Shanghainese", "上海话", "南京话", "西安话", "重庆话", "唐山话", "温州话", "武汉话",
                "Shanxi", "陕西话", "福建话", "四川话", "山西话", "贵州话", "Min Nan", "Hokkien", "闽南语"}));
        Arrays.stream(Locale.getISOLanguages()).forEach(la -> LANGUAGES.add(of(la, null)));
    }

    protected Language(String code, String text, String title) {
        super(code, text, title, null);
    }

    protected Language(String code, String text, String title, String[] aka) {
        super(code, text, title, aka);
    }

    /**
     * Obtains an instance of language from code.
     */
    public static Language ofCode(String code) {
        return AssertUtils.findOne(LANGUAGES.stream(), l -> l.code.equals(code),
                "Unknown language code %s", code);
    }

    /**
     * Obtains an instance of language from English text.
     */
    public static Language ofText(String text) {
        return AssertUtils.findOne(LANGUAGES.stream(), l -> l.text.equals(text) || l.alsoKnownAs(text),
                "Unknown language text %s", text);
    }

    /**
     * Obtains an instance of language from Chinese title
     */
    public static Language ofTitle(String title) {
        return AssertUtils.findOne(LANGUAGES.stream(), l -> l.title.equals(title) || l.alsoKnownAs(title),
                "Unknown language title %s", title);
    }

    private static Language of(String code, String[] aka) {
        Locale locale = new Locale(code);
        return new Language(locale.getLanguage(), locale.getDisplayLanguage(Locale.ENGLISH),
                locale.getDisplayLanguage(Locale.CHINESE), aka);
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
        if (obj instanceof Language) {
            Language other = (Language) obj;
            return code.equals(other.code);
        }
        return false;
    }
}
