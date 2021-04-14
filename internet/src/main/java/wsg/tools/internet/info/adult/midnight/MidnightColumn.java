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
    COLLECTION(27, "youyou", 52),
    /**
     * @see <a href="https://www.shenyequ.com/wutu/">Entries</a>
     */
    ENTRY(29, "wutu", 143),
    /**
     * @see <a href="https://www.shenyequ.com/xiezhen/">Albums</a>
     */
    ALBUM(30, "xiezhen", 597),
    /**
     * @see <a href="https://www.shenyequ.com/ara/">ARA Amateurs</a>
     */
    ARA(31, "ara", 737),
    /**
     * @see <a href="https://www.shenyequ.com/259LUXU/">ラグジュTV Amateurs</a>
     */
    LUXU(32, "259LUXU", 1314),
    /**
     * @see <a href="https://www.shenyequ.com/prestige/">Prestige Amateurs</a>
     */
    PRESTIGE(33, "prestige", 12417);

    private final int id;
    private final String text;
    private final int first;

    MidnightColumn(int id, String text, int first) {
        this.id = id;
        this.text = text;
        this.first = first;
    }

    public int getId() {
        return id;
    }

    public int getFirst() {
        return first;
    }

    @Override
    public String getText() {
        return text;
    }
}
