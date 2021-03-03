package wsg.tools.internet.video.site.adult;

import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.function.CodeSupplier;

/**
 * Base entry of an adult video only with a code.
 *
 * @author Kingen
 * @since 2021/2/28
 */
public class BaseAdultEntry implements CodeSupplier<String> {

    private final String code;

    protected BaseAdultEntry(String code) {
        this.code = AssertUtils.requireNotBlank(code);
    }

    @Override
    public String getCode() {
        return code;
    }
}
