package wsg.tools.internet.resource.entity.item.base;

/**
 * Supply type of the item.
 *
 * @author Kingen
 * @since 2020/11/23
 */
@FunctionalInterface
public interface TypeSupplier {

    /**
     * Obtains type of the item.
     *
     * @return type
     */
    VideoType getType();
}