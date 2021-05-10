package wsg.tools.internet.info.adult.view;

import wsg.tools.internet.base.EntityProperty;

/**
 * Represents a supplier of the serial number of the adult entry.
 *
 * @author Kingen
 * @since 2021/4/7
 */
@EntityProperty
public interface SerialNumSupplier {

    /**
     * Returns the serial number of the adult entry.
     *
     * @return the serial number
     */
    String getSerialNum();
}
