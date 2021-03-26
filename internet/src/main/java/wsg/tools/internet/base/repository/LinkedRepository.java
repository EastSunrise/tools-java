package wsg.tools.internet.base.repository;

import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * A repository whose entities are doubly-linked.
 *
 * @author Kingen
 * @see LinkedRepoIterator
 * @since 2021/3/9
 */
public interface LinkedRepository<ID, T> extends Repository<ID, T>, RepoIterable<T> {

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
     * Returns the last identifier in the repository or {@code null} if this repository is empty.
     *
     * @return the last identifier, or {@code null} if this repository is empty
     */
    ID lastIdentifier();

    /**
     * Retrieves the last entity in the repository or {@code null} if this repository is empty.
     *
     * @return the last entity, or {@code null} if this repository is empty
     * @throws NotFoundException      if the last entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    default T last() throws NotFoundException, OtherResponseException {
        ID id = lastIdentifier();
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
     * Returns a doubly-linked iterator over the identifiers and entities in this repository.
     *
     * @return a doubly-linked iterator
     */
    @Nonnull
    LinkedRepoIterator<ID, T> linkedRepoIterator();

    /**
     * Returns a doubly-linked iterator over the identifiers and entities in this repository,
     * starting with the given identifier.
     *
     * @param id identifier of the first entity to be returned from the repository iterator (by a
     *           call to {@link LinkedRepoIterator#next})
     * @return a doubly-linked iterator
     */
    @Nonnull
    LinkedRepoIterator<ID, T> linkedRepoIterator(ID id);
}
