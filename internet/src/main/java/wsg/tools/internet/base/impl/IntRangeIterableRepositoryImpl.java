package wsg.tools.internet.base.impl;

import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

import javax.annotation.Nonnull;
import java.util.function.Supplier;
import java.util.stream.IntStream;

/**
 * An implementation of {@link IterableRepository}.
 * <p>
 * The iteration of the repository will go on with that of an iterator over an integer
 * range whose lower and upper bounds are supplied by {@link #minSupplier} and {@link #maxSupplier}.
 * <p>
 *
 * @author Kingen
 * @since 2021/1/12
 */
public class IntRangeIterableRepositoryImpl<T> implements IterableRepository<T> {

    private final Repository<Integer, T> repository;
    private final Supplier<Integer> minSupplier = () -> 1;
    private final Supplier<Integer> maxSupplier;

    public IntRangeIterableRepositoryImpl(@Nonnull Repository<Integer, T> repository, int max) {
        this.repository = repository;
        this.maxSupplier = () -> max;
    }

    public IntRangeIterableRepositoryImpl(@Nonnull Repository<Integer, T> repository, @Nonnull Supplier<Integer> maxSupplier) {
        this.repository = repository;
        this.maxSupplier = maxSupplier;
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return new IdentifiedRepositoryIterator<>(repository, IntStream.rangeClosed(minSupplier.get(), maxSupplier.get()).iterator());
    }
}
