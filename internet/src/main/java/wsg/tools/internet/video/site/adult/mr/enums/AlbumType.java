package wsg.tools.internet.video.site.adult.mr.enums;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Type of an album.
 *
 * @author Kingen
 * @since 2021/2/26
 */
public enum AlbumType implements TextSupplier {

    PHOTO("xiezhen", 1),
    LIFE("life", 8),
    SCENE("scene", 31);

    private final String text;
    private final int first;

    AlbumType(String text, int first) {
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
