package wsg.tools.internet.base.impl;

import java.util.Collection;
import java.util.stream.Stream;
import wsg.tools.internet.base.intf.IntIndicesRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

/**
 * Base implementation of {@link IntIndicesRepository}.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public final class IntIndicesRepositoryImpl<T> implements IntIndicesRepository<T> {

    private final Repository<Integer, T> repository;
    private final Collection<Integer> ids;

    IntIndicesRepositoryImpl(Repository<Integer, T> repository, Collection<Integer> ids) {
        this.repository = repository;
        this.ids = ids;
    }

    @Override
    public RepositoryIterator<T> iteratorRangeClosed(int startInclusive, int endInclusive) {
        Stream<Integer> stream = ids.stream().filter(integer -> integer >= startInclusive)
            .filter(integer -> integer <= endInclusive);
        return new IdentifiedIteratorImpl<>(repository, stream.iterator());
    }

    @Override
    public RepositoryIterator<T> iteratorAfter(int startInclusive) {
        Stream<Integer> stream = ids.stream().filter(integer -> integer >= startInclusive);
        return new IdentifiedIteratorImpl<>(repository, stream.iterator());
    }

    @Override
    public RepositoryIterator<T> iteratorBefore(int endInclusive) {
        Stream<Integer> stream = ids.stream().filter(integer -> integer <= endInclusive);
        return new IdentifiedIteratorImpl<>(repository, stream.iterator());
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return new IdentifiedIteratorImpl<>(repository, ids.iterator());
    }
}
