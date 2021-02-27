package wsg.tools.internet.video.site.adult.mr.enums;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.util.function.AkaPredicate;

/**
 * Nation of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum Nation implements AkaPredicate<String> {


    HAN("汉族", "汉"),
    HUI("回族"),
    MANCHU("满族"),
    MONGOLIAN("蒙古族"),
    UIGHUR("维吾尔族"),
    TIBETAN("藏族"),
    BOURAU("壮族"),
    KAZAKH("哈萨克族"),
    TUCHIA("土家族"),
    YI("彝族"),
    BAI("白族"),
    SHUI("水族"),
    MIAO("苗族"),
    GAOSHAN("高山族"),
    PUYUMA("卑南族", "卑南族原住民"),
    AMIS("阿美族"),
    XIBE("锡伯族"),
    SHE("畲族"),
    HEZHEN("赫哲族"),
    YAMATO("大和民族", "大和族", "大和", "和族"),
    KOREAN("朝鲜族", "大韩民族"),
    MALAY("马来族", "巫族"),
    MIXED("混血");

    public static final String MIXED_KEY = "混血";

    private final String[] aka;

    Nation(String... aka) {this.aka = aka;}

    @Override
    public boolean alsoKnownAs(String other) {
        return ArrayUtils.contains(aka, other);
    }
}
