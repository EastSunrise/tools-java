package wsg.tools.internet.base.impl;

import java.util.NoSuchElementException;
import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.common.NextSupplier;

/**
 * Base implementation of {@link RepositoryIterator}.
 * <p>
 * Each node of the iterator contains an {@link #nextId}, the identifier of next node, which can be
 * used to find next record by the {@link #repository}.
 *
 * @param <T> type of records which is required to implement {@link NextSupplier} to supply the
 *            identifier of next record.
 * @author Kingen
 * @see IterableRepositoryImpl
 * @since 2021/3/2
 */
public class RepositoryIteratorImpl<ID, T extends NextSupplier<ID>> implements RepositoryIterator<T> {

    private final Repository<ID, T> repository;
    private ID nextId;

    public RepositoryIteratorImpl(Repository<ID, T> repository, ID nextId) {
        this.repository = repository;
        this.nextId = nextId;
    }

    @Override
    public boolean hasNext() {
        return nextId != null;
    }

    @Override
    public T next() throws HttpResponseException {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        T t = repository.findById(nextId);
        nextId = t.next();
        return t;
    }
}
