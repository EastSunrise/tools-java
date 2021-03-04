package wsg.tools.internet.resource.movie;

import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.resource.common.VideoTypeSupplier;

/**
 * The genre of a vod item in the {@link GrapeSite}.
 *
 * @author Kingen
 * @since 2021/3/4
 */
public enum GrapeVodGenre implements TextSupplier, VideoTypeSupplier {
    ;

    private final String text;
    private final VideoType type;

    GrapeVodGenre(String text, VideoType type) {
        this.text = text;
        this.type = type;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public VideoType getType() {
        return type;
    }
}
