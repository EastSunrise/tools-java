package wsg.tools.internet.base;

import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

import javax.annotation.Nonnull;
import java.util.Iterator;

/**
 * An implementation of {@link IterableRepository}.
 * <p>
 * The iteration of the repository will go on with that of an iterator
 * over identifiers supplied by {@link #idIterable}.
 *
 * @author Kingen
 * @see IdentifiedRepositoryIterator
 * @since 2021/3/2
 */
public class IdentifiedIterableRepositoryImpl<ID, T> implements IterableRepository<T> {

    private final Repository<ID, T> repository;
    private final Iterable<ID> idIterable;

    public IdentifiedIterableRepositoryImpl(@Nonnull Repository<ID, T> repository, @Nonnull Iterable<ID> idIterable) {
        this.repository = repository;
        this.idIterable = idIterable;
    }

    public IdentifiedIterableRepositoryImpl(@Nonnull Repository<ID, T> repository, @Nonnull Iterator<ID> idIterator) {
        this(repository, () -> idIterator);
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return new IdentifiedRepositoryIterator<>(repository, idIterable.iterator());
    }
}
