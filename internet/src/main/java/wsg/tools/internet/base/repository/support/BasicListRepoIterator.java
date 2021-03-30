package wsg.tools.internet.base.repository.support;

import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.repository.ListRepoIterator;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Base implementation of {@link ListRepoIterator}.
 *
 * @author Kingen
 * @since 2021/3/26
 */
class BasicListRepoIterator<ID, T> implements ListRepoIterator<ID, T> {

    private final RepoRetrievable<ID, T> retrievable;
    private final ListIterator<ID> idListIterator;

    BasicListRepoIterator(RepoRetrievable<ID, T> retrievable, ListIterator<ID> idListIterator) {
        this.retrievable = Objects.requireNonNull(retrievable);
        this.idListIterator = Objects.requireNonNull(idListIterator);
    }

    @Override
    public boolean hasNext() {
        return idListIterator.hasNext();
    }

    @Nonnull
    @Override
    public T next() throws NotFoundException, OtherResponseException {
        if (!hasNext()) {
            throw new NoSuchElementException("Doesn't have next entity.");
        }
        return retrievable.findById(idListIterator.next());
    }

    @Override
    public int nextIndex() {
        return idListIterator.nextIndex();
    }

    @Override
    public boolean hasPrevious() {
        return idListIterator.hasPrevious();
    }

    @Nonnull
    @Override
    public T previous() throws NotFoundException, OtherResponseException {
        if (!hasPrevious()) {
            throw new NoSuchElementException("Doesn't have previous entity.");
        }
        return retrievable.findById(idListIterator.previous());
    }

    @Override
    public int previousIndex() {
        return idListIterator.previousIndex();
    }
}
