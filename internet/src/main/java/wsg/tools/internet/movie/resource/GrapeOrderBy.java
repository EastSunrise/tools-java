package wsg.tools.internet.movie.resource;

import wsg.tools.internet.base.view.PathSupplier;

/**
 * Optional orders when querying items by page.
 *
 * @author Kingen
 * @since 2021/5/28
 */
public enum GrapeOrderBy implements PathSupplier {
    /**
     * The add time
     */
    TIME("addtime"),
    /**
     * The hits
     */
    HITS("hits"),
    /**
     * The rating
     */
    RATING("gold");

    private final String path;

    GrapeOrderBy(String path) {
        this.path = path;
    }

    @Override
    public String getAsPath() {
        return path;
    }
}
