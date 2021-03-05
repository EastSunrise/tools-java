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
     * Common ISO languages
     */
    ZH("Chinese", "汉语"),
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

    /**
     * Non-ISO languages, with three-letter abbreviation as code
     */
    NON("None", "默片"),
    CAN("Cantonese", "粤语", "基本粤语"),
    SIN("Sindarin", "辛达林语"),
    QUE("Quenya", "昆雅语"),
    SIL("Sign Languages", "手语"),
    ASL("American Sign Language", "美国手语"),
    KSL("Korean Sign Language", "韩国手语"),
    BSL("British Sign Language", "英国手语"),
    DOT("Dothraki", "多斯拉克语"),
    KLI("Klingon", "克林贡语"),
    CRE("Creole", "克里奥尔语"),

    /**
     * Other ISO languages
     */
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
    VI("Vietnamese", "越南语");

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
