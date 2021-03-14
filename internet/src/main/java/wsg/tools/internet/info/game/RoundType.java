package wsg.tools.internet.info.game;

import lombok.Getter;
import wsg.tools.common.util.function.IntCodeSupplier;

/**
 * The type of a tournament round.
 *
 * @author Kingen
 * @since 2021/3/13
 */
public enum RoundType implements IntCodeSupplier {
    /**
     * Unknown type
     */
    COMMON(0),
    /**
     * Regular seasons
     */
    REGULAR(1, "常规赛"),
    /**
     * Play-offs
     */
    PLAYOFF(2, "季后赛"),
    /**
     * Qualifying/group phrase
     */
    GROUP(3, "入围赛", "小组赛"),
    /**
     * Elimination/knock-out phrase
     */
    ELIMINATION(4, "淘汰赛"),
    ;

    private final int code;
    @Getter
    private final String[] titles;

    RoundType(int code, String... titles) {
        this.code = code;
        this.titles = titles;
    }

    @Override
    public Integer getCode() {
        return code;
    }
}
