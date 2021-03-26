package wsg.tools.internet.base.repository;

import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * A variant of {@link java.util.Iterator} over a {@code Repository}.
 *
 * @param <T> the type of entities returned by this iterator
 * @author Kingen
 * @see RepoIterable
 * @see Repository
 * @since 2021/3/1
 */
public interface RepoIterator<T> {

    /**
     * Returns {@code true} if the repository has more identifiers.
     *
     * @return {@code true} if the iteration has more identifiers
     */
    boolean hasNext();

    /**
     * Retrieves the next entity in the repository and movies the cursor position.
     *
     * @return the next entity
     * @throws NoSuchElementException if the iteration has no next entity
     * @throws NotFoundException      if the next entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    T next() throws NotFoundException, OtherResponseException;
}
