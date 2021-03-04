package wsg.tools.internet.movie.common.enums;

import wsg.tools.common.util.function.TextSupplier;

/**
 * The source of a rating.
 *
 * @author Kingen
 * @since 2021/2/20
 */
public enum RatingSource implements TextSupplier {
    IMDB("Internet Movie Database"),
    ROTTEN_TOMATOES("Rotten Tomatoes"),
    METACRITIC("Metacritic"),
    ;

    private final String text;

    RatingSource(String text) {this.text = text;}

    @Override
    public String getText() {
        return text;
    }
}
