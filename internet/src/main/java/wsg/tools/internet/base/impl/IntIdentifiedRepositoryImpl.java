package wsg.tools.internet.base.impl;

import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.intf.IntIdentifiedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

/**
 * Base implementation of {@link IntIdentifiedRepository}.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public final class IntIdentifiedRepositoryImpl<T> implements IntIdentifiedRepository<T> {

    private final Repository<Integer, T> repository;
    private final List<Integer> ids;

    IntIdentifiedRepositoryImpl(@Nonnull Repository<Integer, T> repository,
        @Nonnull List<Integer> ids) {
        this.repository = repository;
        this.ids = Collections.unmodifiableList(ids);
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
