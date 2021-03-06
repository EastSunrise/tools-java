package wsg.tools.internet.common.enums;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.util.function.CodeSupplier;

/**
 * The nation of a person.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum Nation implements CodeSupplier {

    /**
     * Part domestic nations
     *
     * @see <a href="http://www.gov.cn/test/2006-04/04/content_244533.htm">
     * Names of Nationalities of China in Romanization with Codes</a>
     */
    HA("Han", "汉族"),
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
    YAM("Yamato", "大和民族"),
    KOR("Korean nationality", "韩民族"),
    MAL("Ethnic Malay", "马来族"),
    MIX("Mixed", "混血");

    private final String enName;
    private final String zhName;

    Nation(String enName, String zhName) {
        this.enName = enName;
        this.zhName = zhName;
    }

    @Nonnull
    @Contract(pure = true)
    @Override
    public String getCode() {
        return name();
    }

    public String getEnName() {
        return enName;
    }

    public String getZhName() {
        return zhName;
    }
}
