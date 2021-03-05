package wsg.tools.internet.base.impl;

import wsg.tools.internet.base.intf.IterableRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.common.NextSupplier;

/**
 * Base implementation of {@link IterableRepository}.
 * <p>
 * Each record of the repository is required to supply the identifier of next record, which means
 * implementing {@link NextSupplier}.
 * <p>
 * The {@link #first} identifier is given to point to the first record.
 *
 * @param <T> type of records which is required to implement {@link NextSupplier} to supply the
 *            identifier of next record.
 * @author Kingen
 * @see RepositoryIteratorImpl
 * @since 2021/3/1
 */
public class IterableRepositoryImpl<ID, T extends NextSupplier<ID>> implements IterableRepository<T> {

    private final Repository<ID, T> repository;
    private final ID first;

    public IterableRepositoryImpl(Repository<ID, T> repository, ID first) {
        this.repository = repository;
        this.first = first;
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return new RepositoryIteratorImpl<>(repository, first);
    }
}
