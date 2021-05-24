package wsg.tools.boot.pojo.entity.base;

import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * Reasons when failed to import an entity.
 *
 * @author Kingen
 * @since 2021/4/15
 */
public enum FailureReason implements IntCodeSupplier {
    EXISTS(101, "The target exists"),
    ARG_INVALID(201, "The argument is invalid"),
    KEY_LACKING(202, "The key is lacking");

    private final int code;
    private final String reason;

    FailureReason(int code, String reason) {
        this.code = code;
        this.reason = reason;
    }

    @Override
    public int getCode() {
        return code;
    }

    public String getReason() {
        return reason;
    }
}
