package wsg.tools.internet.video.enums;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.jackson.intf.AkaPredicate;
import wsg.tools.common.jackson.intf.CodeSupplier;
import wsg.tools.common.jackson.intf.TextSupplier;
import wsg.tools.common.jackson.intf.TitleSupplier;

/**
 * Enum for countries.
 *
 * @author Kingen
 * @since 2020/6/26
 */
public enum CountryEnum implements CodeSupplier<String>, TextSupplier, TitleSupplier, AkaPredicate<String> {
    /**
     * Extended ISO countries.
     */
    CN("China", "中国", new String[]{"中国大陆"}),
    US("United States", "美国", new String[]{"USA"}),
    GB("United Kingdom", "英国", new String[]{"UK"}),
    HK("Hong Kong SAR China", "中国香港特别行政区", new String[]{"Hong Kong", "中国香港"}),
    TW("Taiwan", "台湾", new String[]{"中国台湾"}),
    DE("Germany", "德国", new String[]{"西德", "West Germany"}),
    CZ("Czechia", "捷克", new String[]{"Czech Republic"}),

    /**
     * Other ISO countries
     */
    AD("Andorra", "安道尔"),
    AE("United Arab Emirates", "阿拉伯联合酋长国"),
    AF("Afghanistan", "阿富汗"),
    AG("Antigua & Barbuda", "安提瓜和巴布达"),
    AI("Anguilla", "安圭拉"),
    AL("Albania", "阿尔巴尼亚"),
    AM("Armenia", "亚美尼亚"),
    AO("Angola", "安哥拉"),
    AQ("Antarctica", "南极洲"),
    AR("Argentina", "阿根廷"),
    AS("American Samoa", "美属萨摩亚"),
    AT("Austria", "奥地利"),
    AU("Australia", "澳大利亚"),
    AW("Aruba", "阿鲁巴"),
    AX("Åland Islands", "奥兰群岛"),
    AZ("Azerbaijan", "阿塞拜疆"),
    BA("Bosnia & Herzegovina", "波斯尼亚和黑塞哥维那"),
    BB("Barbados", "巴巴多斯"),
    BD("Bangladesh", "孟加拉国"),
    BE("Belgium", "比利时"),
    BF("Burkina Faso", "布基纳法索"),
    BG("Bulgaria", "保加利亚"),
    BH("Bahrain", "巴林"),
    BI("Burundi", "布隆迪"),
    BJ("Benin", "贝宁"),
    BL("St. Barthélemy", "圣巴泰勒米"),
    BM("Bermuda", "百慕大"),
    BN("Brunei", "文莱"),
    BO("Bolivia", "玻利维亚"),
    BQ("Caribbean Netherlands", "荷属加勒比区"),
    BR("Brazil", "巴西"),
    BS("Bahamas", "巴哈马"),
    BT("Bhutan", "不丹"),
    BV("Bouvet Island", "布韦岛"),
    BW("Botswana", "博茨瓦纳"),
    BY("Belarus", "白俄罗斯"),
    BZ("Belize", "伯利兹"),
    CA("Canada", "加拿大"),
    CC("Cocos (Keeling) Islands", "科科斯（基林）群岛"),
    CD("Congo - Kinshasa", "刚果（金）"),
    CF("Central African Republic", "中非共和国"),
    CG("Congo - Brazzaville", "刚果（布）"),
    CH("Switzerland", "瑞士"),
    CI("Côte d’Ivoire", "科特迪瓦"),
    CK("Cook Islands", "库克群岛"),
    CL("Chile", "智利"),
    CM("Cameroon", "喀麦隆"),
    CO("Colombia", "哥伦比亚"),
    CR("Costa Rica", "哥斯达黎加"),
    CU("Cuba", "古巴"),
    CV("Cape Verde", "佛得角"),
    CW("Curaçao", "库拉索"),
    CX("Christmas Island", "圣诞岛"),
    CY("Cyprus", "塞浦路斯"),
    DJ("Djibouti", "吉布提"),
    DK("Denmark", "丹麦"),
    DM("Dominica", "多米尼克"),
    DO("Dominican Republic", "多米尼加共和国"),
    DZ("Algeria", "阿尔及利亚"),
    EC("Ecuador", "厄瓜多尔"),
    EE("Estonia", "爱沙尼亚"),
    EG("Egypt", "埃及"),
    EH("Western Sahara", "西撒哈拉"),
    ER("Eritrea", "厄立特里亚"),
    ES("Spain", "西班牙"),
    ET("Ethiopia", "埃塞俄比亚"),
    FI("Finland", "芬兰"),
    FJ("Fiji", "斐济"),
    FK("Falkland Islands", "福克兰群岛"),
    FM("Micronesia", "密克罗尼西亚"),
    FO("Faroe Islands", "法罗群岛"),
    FR("France", "法国"),
    GA("Gabon", "加蓬"),
    GD("Grenada", "格林纳达"),
    GE("Georgia", "格鲁吉亚"),
    GF("French Guiana", "法属圭亚那"),
    GG("Guernsey", "根西岛"),
    GH("Ghana", "加纳"),
    GI("Gibraltar", "直布罗陀"),
    GL("Greenland", "格陵兰"),
    GM("Gambia", "冈比亚"),
    GN("Guinea", "几内亚"),
    GP("Guadeloupe", "瓜德罗普"),
    GQ("Equatorial Guinea", "赤道几内亚"),
    GR("Greece", "希腊"),
    GS("South Georgia & South Sandwich Islands", "南乔治亚和南桑威奇群岛"),
    GT("Guatemala", "危地马拉"),
    GU("Guam", "关岛"),
    GW("Guinea-Bissau", "几内亚比绍"),
    GY("Guyana", "圭亚那"),
    HM("Heard & McDonald Islands", "赫德岛和麦克唐纳群岛"),
    HN("Honduras", "洪都拉斯"),
    HR("Croatia", "克罗地亚"),
    HT("Haiti", "海地"),
    HU("Hungary", "匈牙利"),
    ID("Indonesia", "印度尼西亚"),
    IE("Ireland", "爱尔兰"),
    IL("Israel", "以色列"),
    IM("Isle of Man", "马恩岛"),
    IN("India", "印度"),
    IO("British Indian Ocean Territory", "英属印度洋领地"),
    IQ("Iraq", "伊拉克"),
    IR("Iran", "伊朗"),
    IS("Iceland", "冰岛"),
    IT("Italy", "意大利"),
    JE("Jersey", "泽西岛"),
    JM("Jamaica", "牙买加"),
    JO("Jordan", "约旦"),
    JP("Japan", "日本"),
    KE("Kenya", "肯尼亚"),
    KG("Kyrgyzstan", "吉尔吉斯斯坦"),
    KH("Cambodia", "柬埔寨"),
    KI("Kiribati", "基里巴斯"),
    KM("Comoros", "科摩罗"),
    KN("St. Kitts & Nevis", "圣基茨和尼维斯"),
    KP("North Korea", "朝鲜"),
    KR("South Korea", "韩国"),
    KW("Kuwait", "科威特"),
    KY("Cayman Islands", "开曼群岛"),
    KZ("Kazakhstan", "哈萨克斯坦"),
    LA("Laos", "老挝"),
    LB("Lebanon", "黎巴嫩"),
    LC("St. Lucia", "圣卢西亚"),
    LI("Liechtenstein", "列支敦士登"),
    LK("Sri Lanka", "斯里兰卡"),
    LR("Liberia", "利比里亚"),
    LS("Lesotho", "莱索托"),
    LT("Lithuania", "立陶宛"),
    LU("Luxembourg", "卢森堡"),
    LV("Latvia", "拉脱维亚"),
    LY("Libya", "利比亚"),
    MA("Morocco", "摩洛哥"),
    MC("Monaco", "摩纳哥"),
    MD("Moldova", "摩尔多瓦"),
    ME("Montenegro", "黑山"),
    MF("St. Martin", "法属圣马丁"),
    MG("Madagascar", "马达加斯加"),
    MH("Marshall Islands", "马绍尔群岛"),
    MK("Macedonia", "马其顿"),
    ML("Mali", "马里"),
    MM("Myanmar (Burma)", "缅甸"),
    MN("Mongolia", "蒙古"),
    MO("Macau SAR China", "中国澳门特别行政区"),
    MP("Northern Mariana Islands", "北马里亚纳群岛"),
    MQ("Martinique", "马提尼克"),
    MR("Mauritania", "毛里塔尼亚"),
    MS("Montserrat", "蒙特塞拉特"),
    MT("Malta", "马耳他"),
    MU("Mauritius", "毛里求斯"),
    MV("Maldives", "马尔代夫"),
    MW("Malawi", "马拉维"),
    MX("Mexico", "墨西哥"),
    MY("Malaysia", "马来西亚"),
    MZ("Mozambique", "莫桑比克"),
    NA("Namibia", "纳米比亚"),
    NC("New Caledonia", "新喀里多尼亚"),
    NE("Niger", "尼日尔"),
    NF("Norfolk Island", "诺福克岛"),
    NG("Nigeria", "尼日利亚"),
    NI("Nicaragua", "尼加拉瓜"),
    NL("Netherlands", "荷兰"),
    NO("Norway", "挪威"),
    NP("Nepal", "尼泊尔"),
    NR("Nauru", "瑙鲁"),
    NU("Niue", "纽埃"),
    NZ("New Zealand", "新西兰"),
    OM("Oman", "阿曼"),
    PA("Panama", "巴拿马"),
    PE("Peru", "秘鲁"),
    PF("French Polynesia", "法属波利尼西亚"),
    PG("Papua New Guinea", "巴布亚新几内亚"),
    PH("Philippines", "菲律宾"),
    PK("Pakistan", "巴基斯坦"),
    PL("Poland", "波兰"),
    PM("St. Pierre & Miquelon", "圣皮埃尔和密克隆群岛"),
    PN("Pitcairn Islands", "皮特凯恩群岛"),
    PR("Puerto Rico", "波多黎各"),
    PS("Palestinian Territories", "巴勒斯坦领土"),
    PT("Portugal", "葡萄牙"),
    PW("Palau", "帕劳"),
    PY("Paraguay", "巴拉圭"),
    QA("Qatar", "卡塔尔"),
    RE("Réunion", "留尼汪"),
    RO("Romania", "罗马尼亚"),
    RS("Serbia", "塞尔维亚"),
    RU("Russia", "俄罗斯"),
    RW("Rwanda", "卢旺达"),
    SA("Saudi Arabia", "沙特阿拉伯"),
    SB("Solomon Islands", "所罗门群岛"),
    SC("Seychelles", "塞舌尔"),
    SD("Sudan", "苏丹"),
    SE("Sweden", "瑞典"),
    SG("Singapore", "新加坡"),
    SH("St. Helena", "圣赫勒拿"),
    SI("Slovenia", "斯洛文尼亚"),
    SJ("Svalbard & Jan Mayen", "斯瓦尔巴和扬马延"),
    SK("Slovakia", "斯洛伐克"),
    SL("Sierra Leone", "塞拉利昂"),
    SM("San Marino", "圣马力诺"),
    SN("Senegal", "塞内加尔"),
    SO("Somalia", "索马里"),
    SR("Suriname", "苏里南"),
    SS("South Sudan", "南苏丹"),
    ST("São Tomé & Príncipe", "圣多美和普林西比"),
    SV("El Salvador", "萨尔瓦多"),
    SX("Sint Maarten", "荷属圣马丁"),
    SY("Syria", "叙利亚"),
    SZ("Swaziland", "斯威士兰"),
    TC("Turks & Caicos Islands", "特克斯和凯科斯群岛"),
    TD("Chad", "乍得"),
    TF("French Southern Territories", "法属南部领地"),
    TG("Togo", "多哥"),
    TH("Thailand", "泰国"),
    TJ("Tajikistan", "塔吉克斯坦"),
    TK("Tokelau", "托克劳"),
    TL("Timor-Leste", "东帝汶"),
    TM("Turkmenistan", "土库曼斯坦"),
    TN("Tunisia", "突尼斯"),
    TO("Tonga", "汤加"),
    TR("Turkey", "土耳其"),
    TT("Trinidad & Tobago", "特立尼达和多巴哥"),
    TV("Tuvalu", "图瓦卢"),
    TZ("Tanzania", "坦桑尼亚"),
    UA("Ukraine", "乌克兰"),
    UG("Uganda", "乌干达"),
    UM("U.S. Outlying Islands", "美国本土外小岛屿"),
    UY("Uruguay", "乌拉圭"),
    UZ("Uzbekistan", "乌兹别克斯坦"),
    VA("Vatican City", "梵蒂冈"),
    VC("St. Vincent & Grenadines", "圣文森特和格林纳丁斯"),
    VE("Venezuela", "委内瑞拉"),
    VG("British Virgin Islands", "英属维尔京群岛"),
    VI("U.S. Virgin Islands", "美属维尔京群岛"),
    VN("Vietnam", "越南"),
    VU("Vanuatu", "瓦努阿图"),
    WF("Wallis & Futuna", "瓦利斯和富图纳"),
    WS("Samoa", "萨摩亚"),
    YE("Yemen", "也门"),
    YT("Mayotte", "马约特"),
    ZA("South Africa", "南非"),
    ZM("Zambia", "赞比亚"),
    ZW("Zimbabwe", "津巴布韦"),
    ;

    private final String text;
    private final String title;
    private final String[] aka;

    CountryEnum(String text, String title) {
        this(text, title, null);
    }

    CountryEnum(String text, String title, String[] aka) {
        this.text = text;
        this.title = title;
        this.aka = aka;
    }

    @Override
    public String getCode() {
        return name();
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
