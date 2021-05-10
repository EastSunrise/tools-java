package wsg.tools.internet.movie.douban;

import wsg.tools.internet.base.EntityProperty;

/**
 * Supplies the identifier on douban.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@EntityProperty
public interface DoubanIdentifier {

    /**
     * Returns the identifier on douban.
     *
     * @return the identifier
     */
    Long getDbId();
}
