package wsg.tools.internet.resource.site;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Types of the resources under {@link BdFilmSite}.
 *
 * @author Kingen
 * @since 2021/3/1
 */
public enum BdFilmType implements TextSupplier {
    LATEST("zx", 1328),
    HD("gq", 375),
    MANDARIN("gy", 348),
    MICRO("zy", 1257),
    CLASSIC("jd", 359),
    ANIME("dh", 387),
    ;

    private final String text;
    private final int first;

    BdFilmType(String text, int first) {
        this.text = text;
        this.first = first;
    }

    @Override
    public String getText() {
        return text;
    }

    public int getFirst() {
        return first;
    }
}
