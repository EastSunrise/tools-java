package wsg.tools.internet.movie.douban;

import wsg.tools.internet.base.EntityInterface;

/**
 * Supplies the identifier on douban.
 *
 * @author Kingen
 * @since 2020/11/4
 */
@EntityInterface
public interface DoubanIdentifier {

    /**
     * Returns the identifier on douban.
     *
     * @return the identifier
     */
    Long getDbId();
}
