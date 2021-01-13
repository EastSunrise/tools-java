package wsg.tools.internet.resource.site.impl;

import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.entity.item.base.IdentifiedItem;
import wsg.tools.internet.resource.site.intf.RangeRepository;
import wsg.tools.internet.resource.site.intf.ResourceRepository;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Base resource site implementing {@link RangeRepository} and each of its items contains an identifier.
 *
 * @author Kingen
 * @since 2021/1/12
 */
abstract class AbstractRangeResourceSite<I extends IdentifiedItem> extends AbstractResourceSite<I, Integer> implements RangeRepository<I> {

    private final int[] excepts;

    protected AbstractRangeResourceSite(String name, String host, int... excepts) {
        super(name, host);
        this.excepts = excepts;
    }

    protected AbstractRangeResourceSite(String name, String domain, double postPermitsPerSecond, int... excepts) {
        super(name, domain, postPermitsPerSecond);
        this.excepts = excepts;
    }

    protected AbstractRangeResourceSite(String name, SchemeEnum scheme, String domain, double postPermitsPerSecond, int... excepts) {
        super(name, scheme, domain, postPermitsPerSecond);
        this.excepts = excepts;
    }

    @Override
    public List<I> findAllByRangeClosed(@Nullable Integer startInclusive, @Nullable Integer endInclusive) {
        if (startInclusive == null || startInclusive < 1) {
            startInclusive = 1;
        }
        if (endInclusive == null) {
            endInclusive = getMaxId();
        }
        return findAllByRange(this, startInclusive, endInclusive);
    }

    @Override
    public List<I> findAll() {
        return findAllByRange(this, 1, getMaxId());
    }

    /**
     * Obtains items based on ranged ids.
     */
    private <T extends IdentifiedItem> List<T> findAllByRange(ResourceRepository<T, Integer> repository, int startInclusive, int endInclusive) {
        List<T> items = new ArrayList<>();
        for (int id = startInclusive; id <= endInclusive; id++) {
            try {
                items.add(repository.findById(id));
            } catch (NotFoundException ignored) {
            }
        }
        return items;
    }

    @Override
    public I findById(Integer id) throws NotFoundException {
        if (id < 1 || ArrayUtils.contains(excepts, id)) {
            throw new NotFoundException("Not a valid id.");
        }
        return getItem(id);
    }

    /**
     * Obtains current maximum id.
     *
     * @return maximum id
     */
    protected abstract int getMaxId();

    /**
     * Obtains the item of the given id.
     *
     * @param id identifier
     * @return item
     * @throws NotFoundException if not found
     */
    protected abstract I getItem(int id) throws NotFoundException;
}
