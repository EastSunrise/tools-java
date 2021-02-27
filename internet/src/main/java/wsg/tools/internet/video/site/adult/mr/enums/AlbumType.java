package wsg.tools.internet.video.site.adult.mr.enums;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Type of an album.
 *
 * @author Kingen
 * @since 2021/2/26
 */
public enum AlbumType implements TextSupplier {

    PHOTO("xiezhen"),
    LIFE("life"),
    SCENE("scene");

    private final String text;

    AlbumType(String text) {this.text = text;}

    @Override
    public String getText() {
        return text;
    }
}
