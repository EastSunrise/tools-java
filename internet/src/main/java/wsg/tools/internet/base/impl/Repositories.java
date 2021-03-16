package wsg.tools.internet.base.impl;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.Range;
import wsg.tools.internet.base.intf.IndicesRepository;
import wsg.tools.internet.base.intf.IntIndicesRepository;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.common.NextSupplier;

/**
 * Common repositories.
 *
 * @author Kingen
 * @since 2021/3/14
 */
public final class Repositories {

    private Repositories() {
    }

    /**
     * Returns a linked repository starting with the given first identifier.
     */
    public static <ID, T extends NextSupplier<ID>> LinkedRepository<ID, T>
    linked(@Nonnull Repository<ID, T> repository, @Nonnull ID first) {
        return new LinkedRepositoryImpl<>(repository, first);
    }

    /**
     * Returns a repository with given indices.
     */
    public static <ID, T> IndicesRepository<T> indices(
        @Nonnull Repository<ID, T> repository, @Nonnull Collection<ID> ids) {
        return new IndicesRepositoryImpl<>(repository, ids);
    }

    /**
     * Returns a repository with given integer indices.
     */
    public static <V> IntIndicesRepository<V> intIndices(
        @Nonnull Repository<Integer, V> repository, @Nonnull List<Integer> ids) {
        return new IntIndicesRepositoryImpl<>(repository, ids);
    }

    /**
     * Returns a repository whose indices are in the range of the given lower bound and upper
     * bound.
     */
    public static <V> IntIndicesRepository<V> rangeClosed(
        @Nonnull Repository<Integer, V> repository, int startInclusive, int endInclusive) {
        Stream<Integer> stream = IntStream.rangeClosed(startInclusive, endInclusive).boxed();
        List<Integer> ids = stream.collect(Collectors.toList());
        return new IntIndicesRepositoryImpl<>(repository, ids);
    }

    /**
     * Returns a repository whose indices are in the range of the given lower bound and upper bound
     * except those in the {@code excepts}.
     */
    public static <V> IntIndicesRepository<V> rangeClosedExcept(
        @Nonnull Repository<Integer, V> repository, int startInclusive, int endInclusive,
        @Nonnull Range<Integer> excepts) {
        Stream<Integer> stream = IntStream.rangeClosed(startInclusive, endInclusive).boxed();
        List<Integer> ids = stream.filter(id -> !excepts.contains(id)).collect(Collectors.toList());
        return new IntIndicesRepositoryImpl<>(repository, ids);
    }
}
