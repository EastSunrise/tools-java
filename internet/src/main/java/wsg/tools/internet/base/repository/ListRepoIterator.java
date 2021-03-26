package wsg.tools.internet.base.repository;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import javax.annotation.Nonnull;

/**
 * An iterator for {@link ListRepository} that allows to traverse the repository in either direction
 * and obtain the iterator's current position in the repository.
 *
 * @author Kingen
 * @see ListRepository
 * @see RepoIterator
 * @see ListRepository#listRepoIterator()
 * @since 2021/3/26
 */
public interface ListRepoIterator<ID, T> extends LinkedRepoIterator<ID, T> {

    /**
     * Returns the next identifier in the repository and advances the cursor position.
     * <p>
     * Note that this method and {@link #next()} both advance the cursor position when called.
     *
     * @return the next identifier
     * @throws NoSuchElementException if the iteration has no next identifier
     * @see #next()
     */
    @Nonnull
    @Override
    ID nextIdentifier();

    /**
     * Returns the index of the identifier that would be returned by a subsequent call to {@link
     * #nextIdentifier()}. (Returns repository size if the repository iterator is at the end of the
     * repository.)
     *
     * @return the index of the identifier that would be returned by a subsequent call to {@code
     * next}, or repository size if the repository iterator is at the end of the repository
     * @see ListIterator#nextIndex()
     */
    int nextIndex();

    /**
     * Returns the previous identifier in the repository and moves the cursor position backwards.
     * <p>
     * Note that this method and {@link #previous()} both move the cursor position backwards when
     * called.
     *
     * @return the previous identifier
     * @throws NoSuchElementException if the repository has no previous identifier
     */
    @Nonnull
    @Override
    ID previousIdentifier();

    /**
     * Returns the index of the identifier that would be returned by a subsequent call to {@link
     * #previous}. (Returns -1 if the repository iterator is at the beginning of the repository.)
     *
     * @return the index of the identifier that would be returned by a subsequent call to {@code
     * previous}, or -1 if the repository iterator is at the beginning of the repository
     * @see ListIterator#previousIndex()
     */
    int previousIndex();
}
