package wsg.tools.internet.base.repository;

import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * An iterator for doubly-linked repositories that allows to traverse the repository in either
 * directions.
 *
 * @author Kingen
 * @see LinkedRepository
 * @see RepoIterator
 * @see LinkedRepository#linkedRepoIterator()
 * @since 2021/3/26
 */
public interface LinkedRepoIterator<ID, T> extends RepoIterator<T> {

    /**
     * Returns {@code true} if the repository has more identifiers when traversing the repository in
     * the forward direction.
     *
     * @return {@code true} if the iteration has more identifiers in the forward direction.
     */
    @Override
    boolean hasNext();

    /**
     * Returns the next identifier in the repository and advances the cursor position.
     * <p>
     * Note that this method and {@link #next()} both advance the cursor position when called.
     *
     * @return the next identifier
     * @throws NoSuchElementException if the iteration has no next identifier
     * @throws NotFoundException      if the next entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #next()
     */
    @Nonnull
    ID nextIdentifier() throws NotFoundException, OtherResponseException;

    /**
     * Retrieves the next entity in the repository and advances the cursor position.
     * <p>
     * Note that this method and {@link #nextIdentifier()} both advance the cursor position when
     * called.
     *
     * @return the next entity
     * @throws NoSuchElementException if the repository has no next entity
     * @throws NotFoundException      if the next entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #nextIdentifier()
     */
    @Override
    @Nonnull
    T next() throws NotFoundException, OtherResponseException;

    /**
     * Returns {@code true} if the repository has more identifiers when traversing the repository in
     * the reverse direction.
     *
     * @return {@code true} if the iteration has more identifiers in the reverse direction.
     */
    boolean hasPrevious();

    /**
     * Returns the previous identifier in the repository and moves the cursor position backwards.
     * <p>
     * Note that this method and {@link #previous()} both move the cursor position backwards when
     * called.
     *
     * @return the previous identifier
     * @throws NoSuchElementException if the repository has no previous identifier
     * @throws NotFoundException      if the previous entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #previous()
     */
    @Nonnull
    ID previousIdentifier() throws NotFoundException, OtherResponseException;

    /**
     * Retrieves the previous entity in the repository and moves the cursor position backwards.
     * <p>
     * Note that this method and {@link #previousIdentifier()} both move the cursor position
     * backwards when called.
     *
     * @return the previous entity
     * @throws NoSuchElementException if the repository has no previous entity
     * @throws NotFoundException      if the previous entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     * @see #previousIdentifier()
     */
    @Nonnull
    T previous() throws NotFoundException, OtherResponseException;
}
