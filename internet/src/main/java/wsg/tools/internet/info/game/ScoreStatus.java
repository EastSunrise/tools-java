package wsg.tools.internet.info.game;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * The status of a tournament or a match.
 *
 * @author Kingen
 * @since 2021/3/12
 */
public enum ScoreStatus implements IntCodeSupplier, PathSupplier {
    /**
     * Not started
     */
    NOT_STARTED(0, "nostart"),
    ONGOING(1, "ing"),
    FINISHED(2, "end");

    private final int code;
    private final String path;

    ScoreStatus(int code, String path) {
        this.code = code;
        this.path = path;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getAsPath() {
        return path;
    }
}
