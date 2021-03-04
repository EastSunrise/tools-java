package wsg.tools.internet.info.adult;

import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.function.CodeSupplier;

/**
 * An basic entry of an adult video only with a code.
 *
 * @author Kingen
 * @since 2021/2/28
 */
public class BasicAdultEntry implements CodeSupplier<String> {

    private final String code;

    protected BasicAdultEntry(String code) {
        this.code = AssertUtils.requireNotBlank(code);
    }

    @Override
    public String getCode() {
        return code;
    }
}
