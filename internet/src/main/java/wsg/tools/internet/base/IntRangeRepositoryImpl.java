package wsg.tools.internet.base;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import wsg.tools.common.lang.IntIdentifier;
import wsg.tools.internet.common.Scheme;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base resource site implementing both {@link BaseRepository} and {@link RangeRepository} and each of its items contains an integer identifier.
 * <p>
 * The maximum id of the repository is changing continuously, so it's necessary to call {@link #max()} to obtain the latest one
 * and then store it in the field {@link #max}.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public abstract class IntRangeRepositoryImpl<T extends IntIdentifier> extends BaseRepositoryImpl<Integer, T> implements RangeRepository<T, Integer> {

    private static final int MIN = 1;
    private Integer max;

    protected IntRangeRepositoryImpl(String name, String domain) {
        super(name, domain);
    }

    protected IntRangeRepositoryImpl(String name, Scheme scheme, String domain) {
        super(name, scheme, domain);
    }

    protected IntRangeRepositoryImpl(String name, String domain, ResponseHandler<String> handler) {
        super(name, domain, handler);
    }

    protected IntRangeRepositoryImpl(String name, Scheme scheme, String domain, ResponseHandler<String> handler) {
        super(name, scheme, domain, handler);
    }

    @Override
    public List<T> findAllByRangeClosed(@Nullable Integer startInclusive, @Nullable Integer endInclusive) throws HttpResponseException {
        if (startInclusive == null || startInclusive < MIN) {
            startInclusive = MIN;
        }
        int max = getMax();
        if (endInclusive == null || endInclusive > max) {
            endInclusive = max;
        }
        return findAllByRange(this, startInclusive, endInclusive);
    }

    @Override
    public List<T> findAll() throws HttpResponseException {
        return findAllByRange(this, MIN, getMax());
    }

    /**
     * Obtains items based on ranged ids, ignoring those not found.
     */
    private List<T> findAllByRange(BaseRepository<Integer, T> repository, int startInclusive, int endInclusive) throws HttpResponseException {
        List<T> items = new ArrayList<>();
        for (int id = startInclusive; id <= endInclusive; id++) {
            try {
                items.add(repository.findById(id));
            } catch (HttpResponseException e) {
                if (e.getStatusCode() != HttpStatus.SC_NOT_FOUND) {
                    throw e;
                }
            }
        }
        return items;
    }

    @Override
    public T findById(Integer id) throws HttpResponseException {
        if (id < MIN || id > getMax()) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not a valid id.");
        }
        return getItem(id);
    }

    protected final synchronized int getMax() throws HttpResponseException {
        if (max == null) {
            max = max();
        }
        return max;
    }

    /**
     * Supplies minimum id.
     *
     * @return minimum id
     */
    protected int min() {
        return 1;
    }

    /**
     * Supplies latest maximum id.
     *
     * @return maximum id
     * @throws HttpResponseException if an error occurs
     */
    protected abstract int max() throws HttpResponseException;

    /**
     * Obtains the item of the given id.
     *
     * @param id identifier
     * @return item
     * @throws HttpResponseException if an error occurs
     */
    protected abstract T getItem(int id) throws HttpResponseException;
}
