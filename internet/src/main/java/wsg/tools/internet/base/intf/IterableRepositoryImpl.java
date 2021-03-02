package wsg.tools.internet.base.intf;

import wsg.tools.internet.base.BaseRecordIterator;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.RecordIterator;

/**
 * Base implementation of {@link IterableRepository}.
 *
 * @author Kingen
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
    public RecordIterator<T> iterator() {
        return new BaseRecordIterator<>(repository, first);
    }
}
