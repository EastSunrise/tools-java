package wsg.tools.internet.base.impl;

import java.util.Iterator;
import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

/**
 * An implementation of {@link RepositoryIterator}.
 * <p>
 * The iteration of the iterator will go on with that of the {@link #idIterator}, the iterator over
 * identifiers.
 *
 * @author Kingen
 * @since 2021/3/2
 */
class IdentifiedIteratorImpl<ID, T> implements RepositoryIterator<T> {

    private final Repository<ID, T> repository;
    private final Iterator<ID> idIterator;

    IdentifiedIteratorImpl(Repository<ID, T> repository, Iterator<ID> idIterator) {
        this.repository = repository;
        this.idIterator = idIterator;
    }

    @Override
    public boolean hasNext() {
        return idIterator.hasNext();
    }

    @Override
    public T next() throws HttpResponseException {
        return repository.findById(idIterator.next());
    }
}