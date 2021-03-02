package wsg.tools.internet.base.intf;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.IntRangeRecordIterator;
import wsg.tools.internet.base.RecordIterator;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * Base implementation of {@link IntRangeRepository}.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public class IntRangeRepositoryImpl<T> implements IntRangeRepository<T> {

    private final Repository<Integer, T> repository;
    private final Supplier<Integer> maxSupplier;

    public IntRangeRepositoryImpl(Repository<Integer, T> repository, Supplier<Integer> maxSupplier) {
        this.repository = repository;
        this.maxSupplier = maxSupplier;
    }

    @Nonnull
    @Override
    public Integer min() {
        return 1;
    }

    @Nonnull
    @Override
    public Integer max() {
        return maxSupplier.get();
    }

    /**
     * Obtains all records within the range of [{@code startInclusive}, {@code endInclusive}], ignoring those not found.
     */
    @Override
    public List<T> findAllByRangeClosed(@Nonnull Integer startInclusive, @Nonnull Integer endInclusive) throws HttpResponseException {
        List<T> ts = new ArrayList<>();
        for (int id = startInclusive; id <= endInclusive; id++) {
            try {
                ts.add(repository.findById(id));
            } catch (HttpResponseException e) {
                if (e.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                    throw e;
                }
            }
        }
        return ts;
    }

    @Override
    public RecordIterator<T> iterator() throws HttpResponseException {
        return new IntRangeRecordIterator<>(repository, min(), max());
    }
}
