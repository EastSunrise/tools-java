package wsg.tools.internet.video.enums;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.PathParameterized;

/**
 * Enum of marking type.
 *
 * @author Kingen
 * @since 2020/6/23
 */
public enum MarkEnum implements TitleSupplier, PathParameterized {
    /**
     * wish/do/collect
     */
    WISH("想看"),
    DO("在看"),
    COLLECT("看过");

    private final String title;

    MarkEnum(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getPath() {
        return name().toLowerCase();
    }
}
