package wsg.tools.internet.base.repository;

import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * A repository whose entities are singly-linked.
 *
 * @author Kingen
 * @see LinkedRepoIterator
 * @since 2021/3/9
 */
public interface LinkedRepository<ID, T>
    extends Repository<ID, T>, RepoRetrievable<ID, T>, RepoIterable<T> {

    /**
     * Returns {@code true} if this repository contains no identifiers.
     *
     * @return {@code true} if this repository contains no identifiers
     */
    boolean isEmpty();

    /**
     * Returns the first identifier in the repository or {@code null} if this repository is empty.
     *
     * @return the first identifier, or {@code null} if this repository is empty
     */
    ID firstIdentifier();

    /**
     * Retrieves the first entity in the repository or {@code null} if this repository is empty.
     *
     * @return the first entity, or {@code null} if this repository is empty
     * @throws NotFoundException      if the first entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    default T first() throws NotFoundException, OtherResponseException {
        ID id = firstIdentifier();
        return id == null ? null : findById(id);
    }

    /**
     * Returns an iterator over the entities in this repository in proper sequence, from first
     * (head) to last (tail).
     *
     * @return an iterator in proper sequence
     */
    @Override
    @Nonnull
    RepoIterator<T> repoIterator();

    /**
     * Returns a singly-linked iterator over the identifiers and entities in this repository.
     *
     * @return a singly-linked iterator
     */
    @Nonnull
    LinkedRepoIterator<ID, T> linkedRepoIterator();

    /**
     * Returns a singly-linked iterator over the identifiers and entities in this repository,
     * starting with the given identifier.
     *
     * @param id identifier of the first entity to be returned from the repository iterator (by a
     *           call to {@link LinkedRepoIterator#next})
     * @return a singly-linked iterator
     */
    @Nonnull
    LinkedRepoIterator<ID, T> linkedRepoIterator(ID id);
}
