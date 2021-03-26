package wsg.tools.internet.info.adult.wiki;

import wsg.tools.common.util.function.TextSupplier;

/**
 * Type of an album.
 *
 * @author Kingen
 * @since 2021/2/26
 */
public enum WikiAlbumType implements TextSupplier {

    /**
     * @see <a href="http://www.mrenbaike.net/tuku/xiezhen/">Photos</a>
     */
    PHOTO("xiezhen", 1),
    /**
     * @see <a href="http://www.mrenbaike.net/tuku/life/">Life</a>
     */
    LIFE("life", 8),
    /**
     * @see <a href="http://www.mrenbaike.net/tuku/scene/">Scenes</a>
     */
    SCENE("scene", 31);

    private final String text;
    private final int first;

    WikiAlbumType(String text, int first) {
        this.text = text;
        this.first = first;
    }

    @Override
    public String getText() {
        return text;
    }

    public int first() {
        return first;
    }
}
