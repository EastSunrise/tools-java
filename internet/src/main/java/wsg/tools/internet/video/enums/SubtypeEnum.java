package wsg.tools.internet.video.enums;

import wsg.tools.common.jackson.intf.AkaSerializable;

/**
 * Subtype of subjects.
 *
 * @author Kingen
 * @since 2020/6/17
 */
public enum SubtypeEnum implements AkaSerializable<String> {
    /**
     * Movie
     */
    MOVIE("movie"),
    TV("tv"),
    TV_SERIES("series", "TVSeries"),
    TV_EPISODE("episode", "TVEpisode"),
    CREATIVE_WORK("CreativeWork");

    private String[] aka;

    SubtypeEnum(String... aka) {
        this.aka = aka;
    }

    @Override
    public boolean alsoKnownAs(String other) {
        for (String s : aka) {
            if (s.equalsIgnoreCase(other)) {
                return true;
            }
        }
        return false;
    }
}
