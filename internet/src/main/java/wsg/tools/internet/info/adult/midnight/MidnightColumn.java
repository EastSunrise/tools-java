package wsg.tools.internet.info.adult.midnight;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Columns of {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum MidnightColumn implements TextSupplier {

    /**
     * @see <a href="https://www.shenyequ.com/youyou/">Collections</a>
     */
    COLLECTION(27, "youyou"),
    /**
     * @see <a href="https://www.shenyequ.com/wutu/">Entries</a>
     */
    ENTRY(29, "wutu"),
    /**
     * @see <a href="https://www.shenyequ.com/xiezhen/">Albums</a>
     */
    ALBUM(30, "xiezhen"),
    /**
     * @see <a href="https://www.shenyequ.com/ara/">ARA Amateurs</a>
     */
    ARA(31, "ara"),
    /**
     * @see <a href="https://www.shenyequ.com/259LUXU/">ラグジュTV Amateurs</a>
     */
    LUXU(32, "259LUXU"),
    /**
     * @see <a href="https://www.shenyequ.com/prestige/">Prestige Amateurs</a>
     */
    PRESTIGE(33, "prestige");

    private final int code;
    private final String text;

    MidnightColumn(int code, String text) {
        this.code = code;
        this.text = text;
    }

    public int getCode() {
        return code;
    }

    @Override
    public String getText() {
        return text;
    }
}
