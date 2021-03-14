package wsg.tools.internet.info.game;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * The status of a tournament or a match.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public enum ScoreStatus implements IntCodeSupplier, TextSupplier {
    /**
     * Not started
     */
    NOT_STARTED(0, "nostart"),
    ONGOING(1, "ing"),
    FINISHED(2, "end");

    private final int code;
    private final String text;

    ScoreStatus(int code, String text) {
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
