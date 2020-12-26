package wsg.tools.internet.resource.entity.rrys.common;

import wsg.tools.common.util.function.CodeSupplier;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.resource.site.RrysSite;

/**
 * Groups of users of {@link RrysSite}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public enum CommonGroupEnum implements CodeSupplier<Integer>, TextSupplier {

    BANNED(-3, "禁止发言"),
    FORBIDDEN(-2, "禁止访问"),
    LV1(52, "LV1"),
    LV2(53, "LV2"),
    LV3(54, "LV3"),
    LV4(55, "LV4"),
    LV5(56, "LV5"),
    LV6(59, "LV6"),
    ;

    private final int code;
    private final String text;

    CommonGroupEnum(int code, String text) {
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
