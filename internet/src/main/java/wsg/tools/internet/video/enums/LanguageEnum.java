package wsg.tools.internet.video.enums;

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
public enum LanguageEnum implements CodeSupplier<String>, TextSupplier, TitleSupplier, AkaPredicate<String> {
    /**
     * Extended ISO languages
     */
    ZH("Chinese", "汉语", new String[]{"汉语普通话", "普通话", "Mandarin", "中文", "国语", "汉语（普通话）", "中文（普通话）", "华语", "基本普通话",
            "粤语", "Cantonese", "基本粤语", "闽南语", "闽南话", "闽南语（台语）", "Hokkien", "客家话", "客家语", "客语", "Hakka", "卑南语", "Min Nan", "潮汕话", "潮州话", "吴语", "阿美语",
            "桂柳话", "满语",
            "广东话", "福建话", "台语", "浙江话", "湖南话", "湖南方言", "湖北话", "云南方言", "安徽方言", "四川话", "陕西话", "陕西方言", "Shanxi", "山西话", "山东话", "河南方言",
            "甘肃方言", "贵州话", "贵州方言", "东北话",
            "北京话", "Shanghainese", "上海话", "沪语", "南京话", "天津话", "西安话", "重庆话", "唐山话", "福州话", "温州话", "武汉话", "芜湖话", "长沙话", "青岛话", "无锡话", "合肥话",
            "贵州独山话", "江油话", "永州话"}),
    EN("English", "英语", new String[]{"英文", "基本英语", "Old English", "古英语", "古代英语"}),
    JA("Japanese", "日语", new String[]{"日本语", "日文"}),
    KO("Korean", "韩语", new String[]{"釜山方言", "韩文"}),
    FR("French", "法语", new String[]{"法文"}),
    HI("Hindi", "印地语", new String[]{"北印度语"}),
    ES("Spanish", "西班牙语"),
    RU("Russian", "俄语", new String[]{"俄语 Russian"}),
    AR("Arabic", "阿拉伯语"),
    IT("Italian", "意大利语", new String[]{"Sicilian", "西西里语"}),
    TH("Thai", "泰语"),
    EL("Greek", "希腊语", new String[]{"Greek, Ancient (to 1453)", "Ancient (to 1453)", "古希腊语"}),
    IN("Indonesian", "印度尼西亚文", new String[]{"印度尼西亚语", "印尼语", "印尼话"}),
    GD("Scottish Gaelic", "苏格兰盖尔语", new String[]{"Scots"}),
    UG("Uyghur", "维吾尔语", new String[]{"Uighur"}),
    IW("Hebrew", "希伯来文", new String[]{"希伯来语"}),
    KL("Kalaallisut", "格陵兰语", new String[]{"Greenlandic"}),
    EU("Basque", "巴斯克语"),
    FIL("Filipino", "菲律宾语"),
    HAW("Hawaiian", "夏威夷语"),
    GSW("Swiss German", "瑞士德语"),
    AF("Afrikaans", "南非荷兰语", new String[]{"南非语"}),
    JI("Yiddish", "依地文", new String[]{"意第绪语"}),
    KW("Cornish", "康沃尔语", new String[]{"科尼什语"}),
    NDS("Low German", "低地德语", new String[]{"Mende", "门德语"}),
    FA("Persian", "波斯语", new String[]{"Dari"}),
    FUR("Friulian", "弗留利语", new String[]{"Fur"}),
    SIO("Sioux", "苏语"),
    NAP("Neapolitan", "那不勒斯语"),
    NAI("North American Indian", "北美印第安语"),
    HUN("Huns", "匈奴语"),
    PAR("Parsee", "帕西语"),
    FLE("Flemish", "弗拉芒语"),
    PAW("Pawnee", "波尼语"),
    LAD("Ladakh", "拉达克语", new String[]{"Ladakhi"}),

    /**
     * Manual languages, three-letter abbreviation as code
     */
    NON("None", "默片"),
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
    AA("Afar", "阿法尔语"),
    AB("Abkhazian", "阿布哈西亚语"),
    AE("Avestan", "阿维斯塔语"),
    AK("Akan", "阿肯语"),
    AM("Amharic", "阿姆哈拉语"),
    AN("Aragonese", "阿拉贡语"),
    AS("Assamese", "阿萨姆语"),
    AV("Avaric", "阿瓦尔语"),
    AY("Aymara", "艾马拉语"),
    AZ("Azerbaijani", "阿塞拜疆语"),
    BA("Bashkir", "巴什基尔语"),
    BE("Belarusian", "白俄罗斯语"),
    BG("Bulgarian", "保加利亚语"),
    BH("Bihari", "比哈尔文"),
    BI("Bislama", "比斯拉马语"),
    BM("Bambara", "班巴拉语"),
    BN("Bangla", "孟加拉语"),
    BO("Tibetan", "藏语"),
    BR("Breton", "布列塔尼语"),
    BS("Bosnian", "波斯尼亚语"),
    CA("Catalan", "加泰罗尼亚语"),
    CE("Chechen", "车臣语"),
    CH("Chamorro", "查莫罗语"),
    CO("Corsican", "科西嘉语"),
    CR("Cree", "克里族语"),
    CS("Czech", "捷克语"),
    CU("Church Slavic", "教会斯拉夫语"),
    CV("Chuvash", "楚瓦什语"),
    CY("Welsh", "威尔士语"),
    DA("Danish", "丹麦语"),
    DE("German", "德语"),
    DV("Divehi", "迪维希语"),
    DZ("Dzongkha", "宗卡语"),
    EE("Ewe", "埃维语"),
    EO("Esperanto", "世界语"),
    ET("Estonian", "爱沙尼亚语"),
    FF("Fulah", "富拉语"),
    FI("Finnish", "芬兰语"),
    FJ("Fijian", "斐济语"),
    FO("Faroese", "法罗语"),
    FY("Western Frisian", "西弗里西亚语"),
    GA("Irish", "爱尔兰语"),
    GL("Galician", "加利西亚语"),
    GN("Guarani", "瓜拉尼语"),
    GU("Gujarati", "古吉拉特语"),
    GV("Manx", "马恩语"),
    HA("Hausa", "豪萨语"),
    HO("Hiri Motu", "希里莫图语"),
    HR("Croatian", "克罗地亚语"),
    HT("Haitian Creole", "海地克里奥尔语"),
    HU("Hungarian", "匈牙利语"),
    HY("Armenian", "亚美尼亚语"),
    HZ("Herero", "赫雷罗语"),
    IA("Interlingua", "国际语"),
    IE("Interlingue", "国际文字（E）"),
    IG("Igbo", "伊博语"),
    II("Sichuan Yi", "四川彝语"),
    IK("Inupiaq", "伊努皮克语"),
    IO("Ido", "伊多语"),
    IS("Icelandic", "冰岛语"),
    IU("Inuktitut", "因纽特语"),
    JV("Javanese", "爪哇语"),
    KA("Georgian", "格鲁吉亚语"),
    KG("Kongo", "刚果语"),
    KI("Kikuyu", "吉库尤语"),
    KJ("Kuanyama", "宽亚玛语"),
    KK("Kazakh", "哈萨克语"),
    KM("Khmer", "高棉语"),
    KN("Kannada", "卡纳达语"),
    KR("Kanuri", "卡努里语"),
    KS("Kashmiri", "克什米尔语"),
    KU("Kurdish", "库尔德语"),
    KV("Komi", "科米语"),
    KY("Kyrgyz", "柯尔克孜语"),
    LA("Latin", "拉丁语"),
    LB("Luxembourgish", "卢森堡语"),
    LG("Ganda", "卢干达语"),
    LI("Limburgish", "林堡语"),
    LN("Lingala", "林加拉语"),
    LO("Lao", "老挝语"),
    LT("Lithuanian", "立陶宛语"),
    LU("Luba-Katanga", "鲁巴加丹加语"),
    LV("Latvian", "拉脱维亚语"),
    MG("Malagasy", "马拉加斯语"),
    MH("Marshallese", "马绍尔语"),
    MI("Maori", "毛利语"),
    MK("Macedonian", "马其顿语"),
    ML("Malayalam", "马拉雅拉姆语"),
    MN("Mongolian", "蒙古语"),
    MO("Moldavian", "摩尔多瓦文"),
    MR("Marathi", "马拉地语"),
    MS("Malay", "马来语"),
    MT("Maltese", "马耳他语"),
    MY("Burmese", "缅甸语"),
    NA("Nauru", "瑙鲁语"),
    NB("Norwegian Bokmål", "书面挪威语"),
    ND("North Ndebele", "北恩德贝勒语"),
    NE("Nepali", "尼泊尔语"),
    NG("Ndonga", "恩东加语"),
    NL("Dutch", "荷兰语"),
    NN("Norwegian Nynorsk", "挪威尼诺斯克语"),
    NO("Norwegian", "挪威语"),
    NR("South Ndebele", "南恩德贝勒语"),
    NV("Navajo", "纳瓦霍语"),
    NY("Nyanja", "齐切瓦语"),
    OC("Occitan", "奥克语"),
    OJ("Ojibwa", "奥吉布瓦语"),
    OM("Oromo", "奥罗莫语"),
    OR("Odia", "奥里亚语"),
    OS("Ossetic", "奥塞梯语"),
    PA("Punjabi", "旁遮普语"),
    PI("Pali", "巴利语"),
    PL("Polish", "波兰语"),
    PS("Pashto", "普什图语"),
    PT("Portuguese", "葡萄牙语"),
    QU("Quechua", "克丘亚语"),
    RM("Romansh", "罗曼什语"),
    RN("Rundi", "隆迪语"),
    RO("Romanian", "罗马尼亚语"),
    RW("Kinyarwanda", "卢旺达语"),
    SA("Sanskrit", "梵语"),
    SC("Sardinian", "萨丁语"),
    SD("Sindhi", "信德语"),
    SE("Northern Sami", "北方萨米语"),
    SG("Sango", "桑戈语"),
    SI("Sinhala", "僧伽罗语"),
    SK("Slovak", "斯洛伐克语"),
    SL("Slovenian", "斯洛文尼亚语"),
    SM("Samoan", "萨摩亚语"),
    SN("Shona", "绍纳语"),
    SO("Somali", "索马里语"),
    SQ("Albanian", "阿尔巴尼亚语"),
    SR("Serbian", "塞尔维亚语"),
    SS("Swati", "斯瓦蒂语"),
    ST("Southern Sotho", "南索托语"),
    SU("Sundanese", "巽他语"),
    SV("Swedish", "瑞典语"),
    SW("Swahili", "斯瓦希里语"),
    TA("Tamil", "泰米尔语"),
    TE("Telugu", "泰卢固语"),
    TG("Tajik", "塔吉克语"),
    TI("Tigrinya", "提格利尼亚语"),
    TK("Turkmen", "土库曼语"),
    TL("Tagalog", "他加禄语"),
    TN("Tswana", "茨瓦纳语"),
    TO("Tongan", "汤加语"),
    TR("Turkish", "土耳其语"),
    TS("Tsonga", "聪加语"),
    TT("Tatar", "鞑靼语"),
    TW("Twi", "契维语"),
    TY("Tahitian", "塔希提语"),
    UK("Ukrainian", "乌克兰语"),
    UR("Urdu", "乌尔都语"),
    UZ("Uzbek", "乌兹别克语"),
    VE("Venda", "文达语"),
    VI("Vietnamese", "越南语"),
    VO("Volapük", "沃拉普克语"),
    WA("Walloon", "瓦隆语"),
    WO("Wolof", "沃洛夫语"),
    XH("Xhosa", "科萨语"),
    YO("Yoruba", "约鲁巴语"),
    ZA("Zhuang", "壮语"),
    ZU("Zulu", "祖鲁语"),
    ;

    private final String text;
    private final String title;
    private final String[] aka;

    LanguageEnum(String text, String title) {
        this(text, title, null);
    }

    LanguageEnum(String text, String title, String[] aka) {
        this.text = text;
        this.title = title;
        this.aka = aka;
    }

    @Override
    public String getCode() {
        return name().toLowerCase();
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
