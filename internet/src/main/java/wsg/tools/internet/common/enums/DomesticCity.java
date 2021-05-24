package wsg.tools.internet.common.enums;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Enum for domestic cities.
 *
 * @author Kingen
 * @see <a href="http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/">Codes of Administrative
 * Divisions for Stats</a>
 * @since 2020/7/17
 */
public enum DomesticCity implements IntCodeSupplier {
    /**
     * domestic cities
     */
    PEK(1101, "Beijing", "北京"),
    SHA(3101, "Shanghai", "上海"),
    NKG(3201, "Nanjing", "南京"),
    SZV(3205, "Suzhou", "苏州"),
    HGH(3301, "Hangzhou", "杭州"),
    SZX(4403, "Shenzhen", "深圳"),
    CKG(5001, "Chongqing", "重庆"),
    CTU(5101, "Chengdu", "成都"),
    SIA(6101, "Xian", "西安"),
    ;

    private final int code;
    private final String enName;
    private final String zhName;

    DomesticCity(int code, String enName, String zhName) {
        this.code = code;
        this.enName = enName;
        this.zhName = zhName;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getEnName() {
        return enName;
    }

    public String getZhName() {
        return zhName;
    }
}
