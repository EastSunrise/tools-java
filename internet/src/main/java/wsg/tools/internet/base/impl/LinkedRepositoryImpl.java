package wsg.tools.internet.base.impl;

import java.util.Objects;
import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.common.NextSupplier;

/**
 * An implementation of {@link IterableRepository}.
 * <p>
 * Each record of the repository is required to supply the identifier of next record, which means
 * implementing {@link NextSupplier}.
 * <p>
 * The {@link #first} identifier is given to point to the first record.
 *
 * @param <T> type of records which is required to implement {@link NextSupplier} to supply the
 *            identifier of next record.
 * @author Kingen
 * @see LinkedIteratorImpl
 * @since 2021/3/1
 */
public class LinkedRepositoryImpl<ID, T extends NextSupplier<ID>> implements IterableRepository<T> {

    private final Repository<ID, T> repository;
    private final ID first;

    public LinkedRepositoryImpl(Repository<ID, T> repository, ID first) {
        this.repository = Objects.requireNonNull(repository);
        this.first = Objects.requireNonNull(first);
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return new LinkedIteratorImpl<>(repository, first);
    }
}
