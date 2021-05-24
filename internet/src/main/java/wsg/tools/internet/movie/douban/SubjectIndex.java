package wsg.tools.internet.movie.douban;

import wsg.tools.internet.base.view.SubtypeSupplier;

/**
 * Represents a subject from Douban including basic information.
 *
 * @author Kingen
 * @since 2021/5/15
 */
public interface SubjectIndex extends DoubanIdentifier, SubtypeSupplier<DoubanCatalog> {

    /**
     * Returns the name of the subject.
     *
     * @return the name
     */
    String getName();
}
