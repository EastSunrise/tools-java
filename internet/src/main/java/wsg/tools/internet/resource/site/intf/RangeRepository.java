package wsg.tools.internet.resource.site.intf;

import wsg.tools.internet.resource.entity.item.base.IdentifiedItem;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Repository based on ranged identifiers.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public interface RangeRepository<I extends IdentifiedItem> {

    /**
     * Obtains all items within the range of [{@code startInclusive}, {@code endInclusive}].
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive   the inclusive upper bound
     * @return items
     */
    List<I> findAllByRangeClosed(@Nullable Integer startInclusive, @Nullable Integer endInclusive);
}
