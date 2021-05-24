package wsg.tools.internet.common.enums;

/**
 * Enum for genders.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public enum Gender {
    /**
     * Male
     */
    MALE("男"),
    /**
     * Female
     */
    FEMALE("女"),
    /**
     * Fake female
     */
    FAKE_FEMALE("伪娘");

    private final String displayName;

    Gender(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
