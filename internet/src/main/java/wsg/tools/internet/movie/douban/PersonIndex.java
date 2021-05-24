package wsg.tools.internet.movie.douban;

import wsg.tools.internet.base.view.SubtypeSupplier;

/**
 * Represents a person from Douban including basic information.
 *
 * @author Kingen
 * @since 2021/5/20
 */
public interface PersonIndex extends DoubanIdentifier, SubtypeSupplier<DoubanCatalog> {

    /**
     * Returns the name of the person.
     *
     * @return the name
     */
    String getName();
}
