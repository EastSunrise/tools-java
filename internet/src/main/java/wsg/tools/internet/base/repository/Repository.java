package wsg.tools.internet.base.repository;

import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * The root interface in the <i>repository hierarchy</i>. A repository represents a group of
 * key-value mappings, known as its <i>identifiers-entities</i>, from a site. An entity is to a
 * repository what a record is to a table in the database. There are not any <i>direct</i>
 * implementations of this interface in this module: it provides implementations of more specific
 * subinterfaces like {@link ListRepository} and {@link LinkedRepository}.
 * <p>
 * All general-purpose {@code Repository} implementation classes do not store the actual entities
 * since there are delays between requests and responses from a site. A repository only stores
 * identifiers which are mapped to the actual entities. Like that a map cannot contain duplicate
 * key, a repository cannot contain duplicate identifiers. The difference is that a repository
 * cannot contain duplicate entities neither. Besides, it is <strong>lazily</strong> in a repository
 * to retrieve an entity by a given identifier, namely the method {@link #findById(Object)}, while a
 * map usually contains the values directly.
 *
 * @param <ID> the type of identifiers maintained by this repository
 * @param <T>  the type of mapped entities
 * @author Kingen
 * @see ListRepository
 * @see LinkedRepository
 * @since 2021/1/12
 */
public interface Repository<ID, T> {

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
    T findById(ID id) throws NotFoundException, OtherResponseException;
}
