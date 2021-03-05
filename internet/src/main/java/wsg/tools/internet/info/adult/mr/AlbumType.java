package wsg.tools.internet.info.adult.mr;

import javax.annotation.Nonnull;
import wsg.tools.common.util.function.TextSupplier;
import wsg.tools.internet.common.FirstSupplier;

/**
 * Type of an album.
 *
 * @author Kingen
 * @since 2021/2/26
 */
public enum AlbumType implements TextSupplier, FirstSupplier<Integer> {

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

    AlbumType(String text, int first) {
        this.text = text;
        this.first = first;
    }

    @Override
    public String getText() {
        return text;
    }

    @Nonnull
    @Override
    public Integer first() {
        return first;
    }
}
