package wsg.tools.internet.enums;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.util.function.AkaPredicate;
import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.common.util.function.TitleSupplier;

/**
 * The nation of a person.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum Nation implements CodeSupplier<String>, TextSupplier, TitleSupplier,
    AkaPredicate<String> {

    /**
     * Part domestic nations
     *
     * @see <a href="http://www.gov.cn/test/2006-04/04/content_244533.htm">
     * Names of Nationalities of China in Romanization with Codes</a>
     */
    HA("Han", "汉族", "汉"),
    ZH("Zhuang", "壮族"),
    HU("Hui", "回族"),
    MA("Man", "满族"),
    UG("Uygur", "维吾尔族"),
    MH("Miao", "苗族"),
    YI("Yi", "彝族"),
    TJ("Tujia", "土家族"),
    ZA("Zang", "藏族"),
    MG("Mongol", "蒙古族"),
    BA("Bai", "白族"),
    CS("Chosen", "朝鲜族"),
    KZ("Kazak", "哈萨克族"),
    SH("She", "畲族"),
    GS("Gaoshan", "高山族"),
    SU("Sui", "水族"),
    XB("Xibe", "锡伯族"),
    HZ("Hezhen", "赫哲族"),

    /**
     * Nations in other regions, with three-letter abbreviation as code
     */
    YAM("Yamato", "大和民族", "大和族", "大和", "和族", "やまとみんぞく"),
    KOR("Korean nationality", "韩民族"),
    MAL("Ethnic Malay", "马来族", "巫族"),
    MIX("Mixed", "混血");

    private final String text;
    private final String title;
    private final String[] aka;

    Nation(String text, String title, String... aka) {
        this.text = text;
        this.title = title;
        this.aka = aka;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        return text.equals(other) || title.equals(other) || ArrayUtils.contains(aka, other);
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getText() {
        return text;
    }
}
