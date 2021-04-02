package wsg.tools.internet.common.enums;

import java.util.Locale;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * Enum for regions.
 *
 * @author Kingen
 * @since 2020/6/26
 */
public enum Region
    implements CodeSupplier<String>, TextSupplier, TitleSupplier, AkaPredicate<String> {
    /**
     * ISO regions.
     *
     * @see Locale#getISOCountries()
     */
    WW("World", "世界", "World-wide"),
    CN("China", "中国", "中国大陆", "大陆", "中华人民共和国"),
    US("United States", "美国", "USA"),
    HK("Hong Kong SAR China", "中国香港特别行政区", "Hong Kong", "中国香港", "香港", "中国（香港）"),
    TW("Taiwan", "台湾", "中国台湾", "中国（台湾）", "中国台湾省"),
    MO("Macau SAR China", "中国澳门特别行政区", "中国澳门", "中国（澳门）"),
    JP("Japan", "日本", "日本国"),
    KR("South Korea", "韩国", "大韩民国"),
    IN("India", "印度"),
    GB("United Kingdom", "英国", "UK"),
    FR("France", "法国"),
    RU("Russia", "俄罗斯"),
    DE("Germany", "德国"),
    ES("Spain", "西班牙"),
    IT("Italy", "意大利"),
    AQ("Antarctica", "南极洲"),
    AR("Argentina", "阿根廷"),
    AU("Australia", "澳大利亚"),
    BD("Bangladesh", "孟加拉国"),
    BE("Belgium", "比利时"),
    BR("Brazil", "巴西"),
    CA("Canada", "加拿大"),
    CH("Switzerland", "瑞士"),
    DK("Denmark", "丹麦"),
    EG("Egypt", "埃及"),
    GR("Greece", "希腊"),
    IE("Ireland", "爱尔兰"),
    IL("Israel", "以色列"),
    KZ("Kazakhstan", "哈萨克斯坦"),
    MX("Mexico", "墨西哥"),
    MY("Malaysia", "马来西亚"),
    NL("Netherlands", "荷兰"),
    NZ("New Zealand", "新西兰"),
    PT("Portugal", "葡萄牙"),
    TH("Thailand", "泰国"),
    TR("Turkey", "土耳其"),
    VN("Vietnam", "越南"),
    CZ("Czechia", "捷克"),
    NO("Norway", "挪威"),
    PL("Poland", "波兰"),

    /**
     * Other languages, with three-letter abbreviation as codes except those used in {@link
     * Locale#getAvailableLocales()} and {@link Language}.
     */
    CZS("Czechoslovakia", "捷克斯洛伐克"),
    ;

    private final String text;
    private final String title;
    private final String[] aka;

    Region(String text, String title, String... aka) {
        this.text = text;
        this.title = title;
        this.aka = aka;
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

    @Override
    public String getCode() {
        return name();
    }
}
