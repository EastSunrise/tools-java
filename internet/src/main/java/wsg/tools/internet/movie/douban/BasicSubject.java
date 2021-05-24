package wsg.tools.internet.movie.douban;

/**
 * A simple and basic subject.
 *
 * @author Kingen
 * @since 2021/5/16
 */
class BasicSubject implements SubjectIndex {

    private final long id;
    private final DoubanCatalog catalog;
    private final String name;

    BasicSubject(long id, DoubanCatalog catalog, String name) {
        this.id = id;
        this.catalog = catalog;
        this.name = name;
    }

    @Override
    public Long getDbId() {
        return id;
    }

    @Override
    public DoubanCatalog getSubtype() {
        return catalog;
    }

    @Override
    public String getName() {
        return name;
    }
}
