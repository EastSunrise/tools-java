package wsg.tools.internet.info.adult.midnight;

import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.common.util.function.TextSupplier;

/**
 * Types of items in the {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum MidnightType implements IntCodeSupplier, TextSupplier {

    /**
     * @see <a href="https://www.shenyequ.com/youyou/">Actresses</a>
     */
    ACTRESS(27, "youyou"),
    /**
     * @see <a href="https://www.shenyequ.com/wutu/">Entries</a>
     */
    ENTRY(29, "wutu"),
    /**
     * @see <a href="https://www.shenyequ.com/xiezhen/">Albums</a>
     */
    ALBUM(30, "xiezhen"),
    /**
     * @see <a href="https://www.shenyequ.com/ara/">ARA Laymen</a>
     */
    ARA(31, "ara"),
    /**
     * @see <a href="https://www.shenyequ.com/259LUXU/">ラグジュTV Laymen</a>
     */
    LUXU(32, "259LUXU"),
    /**
     * @see <a href="https://www.shenyequ.com/prestige/">Prestige Laymen</a>
     */
    PRESTIGE(33, "prestige"),
    ;

    private final int code;
    private final String text;

    MidnightType(int code, String text) {
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
