package wsg.tools.internet.movie.resource;

import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.OtherResponseException;

/**
 * The interface that represents a repository of resources.
 *
 * @author Kingen
 * @since 2021/5/31
 */
public interface ResourceRepository<T> extends RepoRetrievable<Integer, T> {

    /**
     * Returns the repository that contains all items of current site.
     *
     * @return the list repository
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    ListRepository<Integer, T> getRepository() throws OtherResponseException;
}
