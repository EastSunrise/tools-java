package wsg.tools.internet.base.repository.support;

import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.repository.LinkedRepoIterator;
import wsg.tools.internet.base.repository.Repository;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Base implementation of {@link LinkedRepoIterator}.
 *
 * @param <ID> type of identifiers
 * @param <T>  type of entities which is required to implement {@link NextSupplier} to supply the
 *             identifier of next entity.
 * @author Kingen
 * @since 2021/3/2
 */
class BasicLinkedRepoIterator<ID, T extends NextSupplier<ID>> implements LinkedRepoIterator<ID, T> {

    private final Repository<ID, T> repository;
    private ID nextId;

    BasicLinkedRepoIterator(Repository<ID, T> repository, ID nextId) {
        this.repository = Objects.requireNonNull(repository);
        this.nextId = nextId;
    }

    @Override
    public boolean hasNext() {
        return nextId != null;
    }

    @Nonnull
    @Override
    public ID nextIdentifier() throws NotFoundException, OtherResponseException {
        if (!hasNext()) {
            throw new NoSuchElementException("Doesn't have next identifier.");
        }
        ID temp = nextId;
        nextId = repository.findById(nextId).nextId();
        return temp;
    }

    @Nonnull
    @Override
    public T next() throws NotFoundException, OtherResponseException {
        T t = repository.findById(nextId);
        nextId = t.nextId();
        return t;
    }

    @Override
    public boolean hasPrevious() {
        throw new UnsupportedOperationException("Can't traverse in reverse direction");
    }

    @Nonnull
    @Override
    public ID previousIdentifier() throws NotFoundException, OtherResponseException {
        throw new UnsupportedOperationException("Can't traverse in reverse direction");
    }

    @Nonnull
    @Override
    public T previous() throws NotFoundException, OtherResponseException {
        throw new UnsupportedOperationException("Can't traverse in reverse direction");
    }
}
