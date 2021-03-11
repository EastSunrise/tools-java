package wsg.tools.internet.base.intf;

/**
 * An extension of {@link IterableRepository}
 * <p>
 * The iteration of the repository will go on with that of an iterator over an integer range whose
 * lower and upper bounds are supplied by {@link #min()} and {@link #max()}.
 * <p>
 * Besides, it provides methods to obtain an iterator over part of the range with given lower bound
 * and upper bound.
 * <p>
 * Since the identifiers in the range are not confirmed to be valid, the implementation is not
 * recommended if the share of the valid identifiers in the whole range is too lower, like less than
 * 95%. It's better to obtain valid identifiers only before constructing the repository for less
 * invalid requests.
 *
 * @author Kingen
 * @since 2021/3/9
 */
public interface IntRangeIdentifiedRepository<T> extends IterableRepository<T> {

    /**
     * Returns the minimum bound of the range.
     *
     * @return the minimum bound
     */
    int min();

    /**
     * Returns the maximum bound of the range.
     *
     * @return the maximum bound
     */
    int max();

    /**
     * Returns an iterator over records within the given range.
     *
     * @param startInclusive the inclusive lower bound of the target range
     * @param endInclusive   the inclusive upper bound of the target range
     * @return an iterator
     */
    RepositoryIterator<T> iterator(int startInclusive, int endInclusive);

    /**
     * Returns an iterator over records after the start bound.
     *
     * @param startInclusive the inclusive lower bound of the target range
     * @return an iterator
     */
    RepositoryIterator<T> iteratorAfter(int startInclusive);

    /**
     * Returns an iterator over records before the end bound.
     *
     * @param endInclusive the inclusive upper bound of the target range
     * @return an iterator
     */
    RepositoryIterator<T> iteratorBefore(int endInclusive);
}
