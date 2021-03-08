package wsg.tools.internet.base.impl;

import java.util.Objects;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

/**
 * An implementation of {@link IterableRepository}.
 * <p>
 * The iteration of the repository will go on with that of an iterator over identifiers supplied by
 * {@link #idIterable}.
 *
 * @author Kingen
 * @see IdentifiedIteratorImpl
 * @since 2021/3/2
 */
public class IdentifiedRepositoryImpl<ID, T> implements IterableRepository<T> {

    private final Repository<ID, T> repository;
    private final Iterable<ID> idIterable;

    public IdentifiedRepositoryImpl(Repository<ID, T> repository, Iterable<ID> idIterable) {
        this.repository = Objects.requireNonNull(repository);
        this.idIterable = Objects.requireNonNull(idIterable);
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return new IdentifiedIteratorImpl<>(repository, idIterable.iterator());
    }
}
