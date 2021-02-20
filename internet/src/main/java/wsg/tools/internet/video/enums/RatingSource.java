package wsg.tools.internet.video.enums;

import wsg.tools.common.util.function.TextSupplier;

/**
 * All sources of rating.
 *
 * @author Kingen
 * @since 2021/2/20
 */
public enum RatingSource implements TextSupplier {
    IMDB("Internet Movie Database");

    private final String text;

    RatingSource(String text) {this.text = text;}

    @Override
    public String getText() {
        return text;
    }
}
