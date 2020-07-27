package wsg.tools.internet.video.enums;

import wsg.tools.common.function.TitleSupplier;

/**
 * Enum for constellations.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public enum ConstellationEnum implements TitleSupplier {
    /**
     * Twelve constellations
     */
    LIBRA("天秤座");

    private final String title;

    ConstellationEnum(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
