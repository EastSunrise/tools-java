package wsg.tools.internet.base.intf;

import org.apache.http.client.HttpResponseException;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * A repository whose records are distributed within a range based on one or more comparable properties.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public interface RangeRepository<T, C extends Comparable<? super C>> {

    /**
     * Returns the lower bound of the range.
     *
     * @return the lower bound
     */
    @Nonnull
    C min();

    /**
     * Returns the upper bound of the range;
     *
     * @return the upper bound
     */
    @Nonnull
    C max();

    /**
     * Obtains all records within the range of [{@code startInclusive}, {@code endInclusive}].
     *
     * @param startInclusive the (inclusive) initial value
     * @param endInclusive   the (inclusive) upper bound
     * @return list of records within the range
     * @throws HttpResponseException if an error occurs.
     */
    List<T> findAllByRangeClosed(@Nonnull C startInclusive, @Nonnull C endInclusive) throws HttpResponseException;
}
