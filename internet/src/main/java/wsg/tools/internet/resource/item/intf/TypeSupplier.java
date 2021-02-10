package wsg.tools.internet.resource.item.intf;

import wsg.tools.internet.resource.item.VideoType;

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