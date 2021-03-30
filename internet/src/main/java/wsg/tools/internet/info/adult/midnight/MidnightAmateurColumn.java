package wsg.tools.internet.info.adult.midnight;

import wsg.tools.internet.info.adult.entry.AmateurAdultEntry;

/**
 * Part columns of {@link MidnightColumn} each of whose items has an {@link AmateurAdultEntry}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum MidnightAmateurColumn {

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

    MidnightAmateurColumn(MidnightColumn column) {
        this.column = column;
    }

    public MidnightColumn getColumn() {
        return column;
    }
}
