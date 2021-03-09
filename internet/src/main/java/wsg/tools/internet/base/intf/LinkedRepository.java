package wsg.tools.internet.base.intf;

import javax.annotation.Nonnull;
import wsg.tools.internet.common.NextSupplier;

/**
 * An extension of {@link IterableRepository}.
 * <p>
 * Each record of the repository is required to supply the identifier of next record, which means
 * implementing {@link NextSupplier}.
 *
 * @param <T> type of records which is required to implement {@link NextSupplier} to provide the
 *            identifier of next record.
 * @author Kingen
 * @since 2021/3/9
 */
public interface LinkedRepository<ID, T extends NextSupplier<ID>> extends IterableRepository<T> {

    /**
     * Returns an iterator over records since the given identifier.
     *
     * @param id the identifier of the record which the iterator starts with.
     * @return an iterator
     */
    RepositoryIterator<T> iteratorAfter(@Nonnull ID id);
}
