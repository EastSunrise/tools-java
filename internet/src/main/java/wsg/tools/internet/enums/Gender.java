package wsg.tools.internet.enums;

import wsg.tools.common.util.function.TitleSupplier;

/**
 * Enum for genders.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public enum Gender implements TitleSupplier {
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

    private final String title;

    Gender(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
