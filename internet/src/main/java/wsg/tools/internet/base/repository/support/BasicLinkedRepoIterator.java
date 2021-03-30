package wsg.tools.internet.base.repository.support;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.repository.LinkedRepoIterator;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Base implementation of {@link LinkedRepoIterator}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
class BasicLinkedRepoIterator<ID, T> implements LinkedRepoIterator<ID, T> {

    private final RepoRetrievable<ID, T> retrievable;
    private final Function<T, ID> next;
    private ID cursor;

    BasicLinkedRepoIterator(RepoRetrievable<ID, T> retrievable, Function<T, ID> next, ID first) {
        this.retrievable = Objects.requireNonNull(retrievable);
        this.next = Objects.requireNonNull(next);
        this.cursor = first;
    }

    @Override
    public boolean hasNext() {
        return cursor != null;
    }

    @Nonnull
    @Override
    public Optional<ID> nextIdentifier() {
        return Optional.ofNullable(cursor);
    }

    @Nonnull
    @Override
    public T next() throws NotFoundException, OtherResponseException {
        if (!hasNext()) {
            throw new NoSuchElementException("Doesn't have next entity.");
        }
        T t = retrievable.findById(cursor);
        try {
            cursor = next.apply(t);
        } catch (NoSuchElementException e) {
            cursor = null;
        }
        return t;
    }
}
