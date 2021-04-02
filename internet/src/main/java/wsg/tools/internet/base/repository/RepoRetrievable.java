package wsg.tools.internet.base.repository;

import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Indicates that the repository is retrievable. The core function is to retrieve an entity by a
 * given identifier.
 *
 * @param <ID> the type of identifiers of entities
 * @param <T>  the type of entities in the repository
 * @author Kingen
 * @since 2021/3/28
 */
public interface RepoRetrievable<ID, T> {

    /**
     * Retrieves an entity by its identifier.
     *
     * @param id must not be {@literal null}
     * @return the entity
     * @throws NullPointerException   if the specified identifier is null
     * @throws NotFoundException      if the entity of the specified identifier is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    T findById(@Nonnull ID id) throws NotFoundException, OtherResponseException;
}
