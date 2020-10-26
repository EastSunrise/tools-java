package wsg.tools.internet.video.enums;

import wsg.tools.common.util.function.TitleSupplier;

/**
 * Enum for roles of a creator in his/her works.
 *
 * @author Kingen
 * @since 2020/7/26
 */
public enum RoleEnum implements TitleSupplier {
    /**
     * cast/director
     */
    SELF("自己"),
    CAST("演员"),
    DUBBER("配音"),
    DIRECTOR("导演"),
    WRITER("编剧"),
    PRODUCER("制片人"),
    ;

    private final String title;

    RoleEnum(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
