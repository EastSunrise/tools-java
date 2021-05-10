package wsg.tools.boot.dao.api.adapter;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.info.adult.view.DurationSupplier;
import wsg.tools.internet.info.adult.view.ImagesSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * Adapter that supplies common properties for adult entries.
 *
 * @author Kingen
 * @since 2021/4/29
 */
public interface AdultEntryAdapter
    extends TitleSupplier, CoverSupplier, DurationSupplier, Tagged, ImagesSupplier {

}
