package wsg.tools.internet.base.repository.support;

import java.util.ListIterator;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.repository.ListRepoIterator;
import wsg.tools.internet.base.repository.Repository;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Base implementation of {@link ListRepoIterator}.
 *
 * @author Kingen
 * @since 2021/3/26
 */
class BasicListRepoIterator<ID, T> implements ListRepoIterator<ID, T> {

    private final Repository<ID, T> repository;
    private final ListIterator<ID> idListIterator;

    BasicListRepoIterator(Repository<ID, T> repository, ListIterator<ID> idListIterator) {
        this.repository = Objects.requireNonNull(repository);
        this.idListIterator = Objects.requireNonNull(idListIterator);
    }

    @Override
    public boolean hasNext() {
        return idListIterator.hasNext();
    }

    @Nonnull
    @Override
    public ID nextIdentifier() {
        return idListIterator.next();
    }

    @Nonnull
    @Override
    public T next() throws NotFoundException, OtherResponseException {
        return repository.findById(nextIdentifier());
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
    public ID previousIdentifier() {
        return idListIterator.previous();
    }

    @Nonnull
    @Override
    public T previous() throws NotFoundException, OtherResponseException {
        return repository.findById(idListIterator.previous());
    }

    @Override
    public int previousIndex() {
        return idListIterator.previousIndex();
    }
}
