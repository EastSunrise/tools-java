package wsg.tools.internet.video.enums;

import wsg.tools.common.util.function.TitleSupplier;

/**
 * Enum for genders.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public enum GenderEnum implements TitleSupplier {
    /**
     * Male/Female
     */
    MALE("男");

    private final String title;

    GenderEnum(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
