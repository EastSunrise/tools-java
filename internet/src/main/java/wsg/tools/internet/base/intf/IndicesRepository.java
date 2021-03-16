package wsg.tools.internet.base.intf;

/**
 * An extension of {@link IterableRepository} based on indices.
 * <p>
 * The iteration of the repository will go on with that of the indices.
 *
 * @author Kingen
 * @since 2021/3/15
 */
@FunctionalInterface
public interface IndicesRepository<T> extends IterableRepository<T> {

}
