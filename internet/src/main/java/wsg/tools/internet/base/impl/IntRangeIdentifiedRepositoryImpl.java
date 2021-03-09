package wsg.tools.internet.base.impl;

import java.util.stream.IntStream;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.intf.IntRangeIdentifiedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

/**
 * Base implementation of {@link IntRangeIdentifiedRepository}.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public class IntRangeIdentifiedRepositoryImpl<T> implements IntRangeIdentifiedRepository<T> {

    private final Repository<Integer, T> repository;
    private final int min;
    private final int max;

    public IntRangeIdentifiedRepositoryImpl(@Nonnull Repository<Integer, T> repository, int min,
        int max) {
        this.repository = repository;
        this.min = min;
        this.max = max;
    }

    public IntRangeIdentifiedRepositoryImpl(Repository<Integer, T> repository, int max) {
        this.repository = repository;
        this.min = 1;
        this.max = max;
    }

    @Override
    public int min() {
        return min;
    }

    @Override
    public int max() {
        return max;
    }

    @Override
    public RepositoryIterator<T> iterator(int startInclusive, int endInclusive) {
        int start = Math.max(startInclusive, min());
        int end = Math.min(endInclusive, max());
        IntStream stream = IntStream.rangeClosed(start, end);
        return new IdentifiedIteratorImpl<>(repository, stream.iterator());
    }

    @Override
    public RepositoryIterator<T> iteratorAfter(int startInclusive) {
        int start = Math.max(startInclusive, min());
        IntStream stream = IntStream.rangeClosed(start, max());
        return new IdentifiedIteratorImpl<>(repository, stream.iterator());
    }

    @Override
    public RepositoryIterator<T> iteratorBefore(int endInclusive) {
        int end = Math.min(endInclusive, max());
        IntStream stream = IntStream.rangeClosed(min(), end);
        return new IdentifiedIteratorImpl<>(repository, stream.iterator());
    }

    @Override
    public RepositoryIterator<T> iterator() {
        IntStream stream = IntStream.rangeClosed(min(), max());
        return new IdentifiedIteratorImpl<>(repository, stream.iterator());
    }
}
