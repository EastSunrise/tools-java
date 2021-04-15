package wsg.tools.boot.pojo.entity.base;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Reasons when failed to import an entity.
 *
 * @author Kingen
 * @since 2021/4/15
 */
public enum FailureReason implements IntCodeSupplier, TextSupplier {
    EXISTS(101, "The target exists"),
    ARG_INVALID(201, "The argument is invalid");

    private final int code;
    private final String text;

    FailureReason(int code, String text) {
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
