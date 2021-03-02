package wsg.tools.internet.base.intf;

/**
 * A repository whose records are distributed within an integer range.
 * It's also a special kind of {@link IterableRepository}.
 *
 * @author Kingen
 * @since 2021/3/2
 */
public interface IntRangeRepository<T> extends RangeRepository<T, Integer>, IterableRepository<T> {
}
