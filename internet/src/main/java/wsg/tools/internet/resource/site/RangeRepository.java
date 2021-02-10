package wsg.tools.internet.resource.site;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Repository based on ranged comparable property.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public interface RangeRepository<T, C extends Comparable<? super C>> {

    /**
     * Obtains all items within the range of [{@code startInclusive}, {@code endInclusive}].
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive   the inclusive upper bound
     * @return items
     */
    List<T> findAllByRangeClosed(@Nullable C startInclusive, @Nullable C endInclusive);
}
