package wsg.tools.internet.resource.common;

/**
 * Supply type of the item.
 *
 * @author Kingen
 * @since 2020/11/23
 */
@FunctionalInterface
public interface VideoTypeSupplier {

    /**
     * Obtains type of the item.
     *
     * @return type
     */
    VideoType getType();
}