package wsg.tools.internet.resource.site;

import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.resource.item.VideoType;
import wsg.tools.internet.resource.item.intf.TypeSupplier;

/**
 * The genre of a vod item in the {@link GrapeSite}.
 *
 * @author Kingen
 * @since 2021/3/4
 */
public enum GrapeVodGenre implements TextSupplier, TypeSupplier {
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
