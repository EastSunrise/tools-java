package wsg.tools.internet.movie.douban;

/**
 * The basic implementation of {@link PersonIndex}.
 *
 * @author Kingen
 * @since 2021/5/20
 */
class BasicPerson extends BasicSubject implements PersonIndex {

    BasicPerson(long id, DoubanCatalog catalog, String name) {
        super(id, catalog, name);
    }
}
