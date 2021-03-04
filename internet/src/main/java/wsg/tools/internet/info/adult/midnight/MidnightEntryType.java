package wsg.tools.internet.info.adult.midnight;

/**
 * Part types of {@link MidnightType} each of whose items has an adult entry.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public enum MidnightEntryType {

    /**
     * @see MidnightType#ENTRY
     */
    ENTRY(MidnightType.ENTRY),
    /**
     * @see MidnightType#ARA
     */
    ARA(MidnightType.ARA),
    /**
     * @see MidnightType#LUXU
     */
    LUXU(MidnightType.LUXU),
    /**
     * @see MidnightType#PRESTIGE
     */
    PRESTIGE(MidnightType.PRESTIGE);

    private final MidnightType type;

    MidnightEntryType(MidnightType type) {
        this.type = type;
    }

    public MidnightType getType() {
        return type;
    }
}
