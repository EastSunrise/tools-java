package wsg.tools.internet.base;

import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.intf.Repository;

import java.util.NoSuchElementException;

/**
 * Base implementation of {@link RecordIterator}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public class BaseRecordIterator<ID, T extends NextSupplier<ID>> implements RecordIterator<T> {

    private final Repository<ID, T> repository;
    private ID nextId;

    public BaseRecordIterator(Repository<ID, T> repository, ID nextId) {
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
