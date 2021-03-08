package wsg.tools.internet.base.impl;

import java.util.NoSuchElementException;
import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.common.NextSupplier;

/**
 * An implementation of {@link RepositoryIterator}.
 * <p>
 * Each node of the iterator contains an {@link #nextId}, the identifier of next node, which can be
 * used to find next record by the {@link #repository}.
 *
 * @param <T> type of records which is required to implement {@link NextSupplier} to supply the
 *            identifier of next record.
 * @author Kingen
 * @see LinkedRepositoryImpl
 * @since 2021/3/2
 */
public class LinkedIteratorImpl<ID, T extends NextSupplier<ID>> implements
    RepositoryIterator<T> {

    private final Repository<ID, T> repository;
    private ID nextId;

    LinkedIteratorImpl(Repository<ID, T> repository, ID first) {
        this.repository = repository;
        this.nextId = first;
    }

    @Override
    public boolean hasNext() {
        return nextId != null;
    }

    @Override
    public T next() throws HttpResponseException {
        if (!hasNext()) {
            throw new NoSuchElementException("Doesn't have next id.");
        }
        T t = repository.findById(nextId);
        nextId = t.nextId();
        return t;
    }
}
