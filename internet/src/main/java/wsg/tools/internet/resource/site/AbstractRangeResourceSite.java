package wsg.tools.internet.resource.site;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.resource.item.IdentifiedItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base resource site implementing {@code RangeRepository} and each of its items contains an integer identifier.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public abstract class AbstractRangeResourceSite<T extends IdentifiedItem> extends BaseSite implements BaseRepository<Integer, T>, RangeRepository<T, Integer> {

    private Integer min;
    private Integer max;

    protected AbstractRangeResourceSite(String name, String domain) {
        super(name, domain);
    }

    protected AbstractRangeResourceSite(String name, SchemeEnum scheme, String domain) {
        super(name, scheme, domain);
    }

    @Override
    public List<T> findAllByRangeClosed(@Nullable Integer startInclusive, @Nullable Integer endInclusive) throws HttpResponseException {
        int min = getMin();
        if (startInclusive == null || startInclusive < min) {
            startInclusive = min;
        }
        int max = getMax();
        if (endInclusive == null || endInclusive > max) {
            endInclusive = max;
        }
        return findAllByRange(this, startInclusive, endInclusive);
    }

    @Override
    public List<T> findAll() throws HttpResponseException {
        return findAllByRange(this, getMin(), getMax());
    }

    /**
     * Obtains items based on ranged ids.
     */
    private List<T> findAllByRange(BaseRepository<Integer, T> repository, int startInclusive, int endInclusive) throws HttpResponseException {
        List<T> items = new ArrayList<>();
        for (int id = startInclusive; id <= endInclusive; id++) {
            items.add(repository.findById(id));
        }
        return items;
    }

    @Override
    public T findById(Integer id) throws HttpResponseException {
        if (id < getMin() || id > getMax()) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, "Not a valid id.");
        }
        return getItem(id);
    }

    private synchronized int getMin() {
        if (min == null) {
            min = min();
        }
        return min;
    }

    private synchronized int getMax() throws HttpResponseException {
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
