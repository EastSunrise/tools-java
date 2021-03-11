package wsg.tools.internet.info.adult.common;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Enum for headers of serial numbers.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public enum SerialNumHeader implements IntCodeSupplier, TextSupplier {
    /**
     * Various headers of serial numbers
     */
    SIRO(1001, "SIRO"),
    GANA_200(1200, "200GANA"),
    SCUTE_229(1229, "229SCUTE"),
    LUXU_259(1259, "259LUXU"),
    ARA_261(1261, "261ARA"),
    DCV_277(1277, "277DCV"),
    MAAN_300(1300, "300MAAN"),
    MIUM_300(2300, "300MIUM"),
    ;

    private final int code;
    private final String text;

    SerialNumHeader(int code, String text) {
        this.code = code;
        this.text = text;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getText() {
        return text;
    }
}
