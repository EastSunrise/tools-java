package wsg.tools.internet.base.repository;

import java.util.NoSuchElementException;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * A variant of {@code Iterator} over entities of a repository.
 *
 * @param <T> the type of entities returned by this iterator
 * @author Kingen
 * @see RepoIterable
 * @since 2021/3/1
 */
public interface RepoIterator<T> {

    /**
     * Returns {@code true} if the repository has more entities.
     *
     * @return {@code true} if the iteration has more entities
     */
    boolean hasNext();

    /**
     * Retrieves the next entity in the repository and moves the cursor position.
     *
     * @return the next entity
     * @throws NoSuchElementException if the iteration has no next entity
     * @throws NotFoundException      if the next entity is not found
     * @throws OtherResponseException if an unexpected error occurs when requesting
     */
    @Nonnull
    T next() throws NotFoundException, OtherResponseException;
}
