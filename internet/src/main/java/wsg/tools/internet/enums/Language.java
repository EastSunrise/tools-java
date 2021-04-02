package wsg.tools.internet.enums;

import java.util.Locale;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Enum for languages.
 *
 * @author Kingen
 * @since 2020/6/26
 */
public enum Language implements CodeSupplier<String>, TextSupplier, TitleSupplier,
    AkaPredicate<String> {
    /**
     * ISO languages
     *
     * @see Locale#getISOLanguages()
     */
    ZH("Chinese", "中文"),
    EN("English", "英语", "英文"),
    FR("French", "法语", "法文"),
    RU("Russian", "俄语"),
    AR("Arabic", "阿拉伯语"),
    ES("Spanish", "西班牙语"),
    JA("Japanese", "日语", "日本语", "日文"),
    KO("Korean", "韩语", "韩文"),
    PT("Portuguese", "葡萄牙语"),
    IT("Italian", "意大利语"),
    DE("German", "德语"),
    HI("Hindi", "印地语"),
    TH("Thai", "泰语"),
    EO("Esperanto", "世界语"),
    IA("Interlingua", "国际语"),
    BO("Tibetan", "藏语"),
    EL("Greek", "希腊语"),
    CS("Czech", "捷克语"),
    CY("Welsh", "威尔士语"),
    DA("Danish", "丹麦语"),
    FI("Finnish", "芬兰语"),
    GA("Irish", "爱尔兰语"),
    HU("Hungarian", "匈牙利语"),
    HY("Armenian", "亚美尼亚语"),
    II("Sichuan Yi", "四川彝语"),
    KK("Kazakh", "哈萨克语"),
    LA("Latin", "拉丁语"),
    TR("Turkish", "土耳其语"),
    VI("Vietnamese", "越南语"),
    SO("Somali", "索马里语"),
    UR("Urdu", "乌尔都语"),
    NL("Dutch", "荷兰语"),
    IW("Hebrew", "希伯来文"),
    PL("Polish", "波兰语"),
    KL("Kalaallisut", "格陵兰语"),
    UK("Ukrainian", "乌克兰语"),
    MY("Burmese", "缅甸语"),
    AM("Amharic", "阿姆哈拉语"),
    IN("Indonesian", "印度尼西亚文"),
    ET("Estonian", "爱沙尼亚语"),
    UG("Uyghur", "维吾尔语"),
    SW("Swahili", "斯瓦希里语"),
    XH("Xhosa", "科萨语"),
    ZU("Zulu", "祖鲁语"),
    TA("Tamil", "泰米尔语"),
    NO("Norwegian", "挪威语"),
    MN("Mongolian", "蒙古语"),
    HR("Croatian", "克罗地亚语"),
    EU("Basque", "巴斯克语"),
    AF("Afrikaans", "南非荷兰语"),
    SK("Slovak", "斯洛伐克语"),
    FA("Persian", "波斯语"),
    LB("Luxembourgish", "卢森堡语"),
    LT("Lithuanian", "立陶宛语"),
    SV("Swedish", "瑞典语"),
    JI("Yiddish", "依地文"),

    /**
     * Available non-ISO languages
     *
     * @see Locale#getAvailableLocales()
     */
    YUE("Cantonese", "粤语", "基本粤语"),
    FIL("Filipino", "菲律宾语"),
    GSW("Swiss German", "瑞士德语"),

    /**
     * Other languages, with three-letter abbreviation as codes except those used in {@link
     * Locale#getAvailableLocales()} and {@link Region}.
     */
    NON("None", "默片"),
    SID("Sindarin", "辛达林语"),
    QUN("Quenya", "昆雅语"),
    SIL("Sign Languages", "手语"),
    ASL("American Sign Language", "美国手语"),
    KSL("Korean Sign Language", "韩国手语"),
    BSL("British Sign Language", "英国手语"),
    DOT("Dothraki", "多斯拉克语"),
    KLI("Klingon", "克林贡语"),
    CRO("Creole", "克里奥尔语"),
    PAW("Pawnee", "波尼语"),
    HUS("Huns", "匈奴语"),
    FLE("Flemish", "弗拉芒语"),
    LAD("Ladakh", "拉达克语"),
    NAP("Neapolitan", "那不勒斯语"),
    ;

    private final String text;
    private final String title;
    private final String[] aka;

    Language(String text, String title, String... aka) {
        this.text = text;
        this.title = title;
        this.aka = aka;
    }

    @Override
    public String getCode() {
        return name().toLowerCase(Locale.ENGLISH);
    }

    @Override
    public boolean alsoKnownAs(String other) {
        return text.equals(other) || title.equals(other) || ArrayUtils.contains(aka, other);
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
