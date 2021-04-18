package wsg.tools.internet.info.adult.midnight;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * Columns of {@link MidnightSite}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum MidnightColumn implements PathSupplier {

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
    private final String path;
    private final int first;

    MidnightColumn(int id, String path, int first) {
        this.id = id;
        this.path = path;
        this.first = first;
    }

    @Nonnull
    @Contract(value = " -> new", pure = true)
    public static MidnightColumn[] amateurs() {
        return new MidnightColumn[]{ARA, LUXU, PRESTIGE};
    }

    public int getId() {
        return id;
    }

    public int getFirst() {
        return first;
    }

    public boolean isAmateur() {
        return id == 31 || id == 32 || id == 33;
    }

    @Override
    public String getAsPath() {
        return path;
    }
}
