package wsg.tools.internet.video.site.adult;

import wsg.tools.common.util.function.CodeSupplier;

/**
 * Base adult video only with a code.
 *
 * @author Kingen
 * @since 2021/2/28
 */
public class BaseAdultVideo implements CodeSupplier<String> {

    private final String code;

    protected BaseAdultVideo(String code) {this.code = code;}

    @Override
    public String getCode() {
        return code;
    }
}
