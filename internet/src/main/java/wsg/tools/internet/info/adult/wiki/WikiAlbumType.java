package wsg.tools.internet.info.adult.wiki;

import wsg.tools.internet.base.view.PathSupplier;

/**
 * Type of an album.
 *
 * @author Kingen
 * @since 2021/2/26
 */
public enum WikiAlbumType implements PathSupplier {

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

    private final String path;
    private final int first;

    WikiAlbumType(String path, int first) {
        this.path = path;
        this.first = first;
    }

    public int first() {
        return first;
    }

    @Override
    public String getAsPath() {
        return path;
    }
}
