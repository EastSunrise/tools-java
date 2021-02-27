package wsg.tools.internet.video.site.adult.mr.enums;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Type of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
public enum CelebrityType implements TextSupplier {
    ENTERTAINMENT("yule"),
    SPORTS("tiyu"),
    POLITICS("politics"),
    FINANCE("finance"),
    LITERATURE("wenxue"),
    SOCIETY("shehui"),
    INTERNET("net"),
    OTHER("other");

    private final String text;

    CelebrityType(String text) {this.text = text;}

    @Override
    public String getText() {
        return text;
    }
}
