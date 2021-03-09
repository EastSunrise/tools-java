package wsg.tools.internet.base.impl;

import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.common.NextSupplier;

/**
 * Base implementation of {@link LinkedRepository}.
 * <p>
 * The {@link #first} identifier is given to point to the first record.
 *
 * @author Kingen
 * @see LinkedIteratorImpl
 * @since 2021/3/1
 */
public class LinkedRepositoryImpl<ID, T extends NextSupplier<ID>>
    implements LinkedRepository<ID, T> {

    private final Repository<ID, T> repository;
    private final ID first;

    public LinkedRepositoryImpl(Repository<ID, T> repository, ID first) {
        this.repository = Objects.requireNonNull(repository);
        this.first = Objects.requireNonNull(first);
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return iteratorAfter(first);
    }

    @Override
    public RepositoryIterator<T> iteratorAfter(@Nonnull ID id) {
        return new LinkedIteratorImpl<>(repository, id);
    }
}
