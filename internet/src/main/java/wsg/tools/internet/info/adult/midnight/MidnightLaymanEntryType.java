package wsg.tools.internet.info.adult.midnight;

import wsg.tools.internet.info.adult.common.LaymanAdultEntry;

/**
 * Part types of {@link MidnightColumn} each of whose items has a {@link LaymanAdultEntry}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum MidnightLaymanEntryType {

    /**
     * @see MidnightColumn#ARA
     */
    ARA(MidnightColumn.ARA),
    /**
     * @see MidnightColumn#LUXU
     */
    LUXU(MidnightColumn.LUXU),
    /**
     * @see MidnightColumn#PRESTIGE
     */
    PRESTIGE(MidnightColumn.PRESTIGE);

    private final MidnightColumn column;

    MidnightLaymanEntryType(MidnightColumn column) {
        this.column = column;
    }

    public MidnightColumn getColumn() {
        return column;
    }
}
