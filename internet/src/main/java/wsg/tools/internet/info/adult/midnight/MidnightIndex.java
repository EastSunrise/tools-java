package wsg.tools.internet.info.adult.midnight;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.base.view.UpdateDatetimeSupplier;

/**
 * An index pointing to a {@link BaseMidnightItem} in the {@link MidnightSite}.
 *
 * @author Kingen
 * @see MidnightSite#findPage(MidnightColumn, MidnightPageReq)
 * @since 2021/3/8
 */
public interface MidnightIndex extends IntIdentifier, TitleSupplier, UpdateDatetimeSupplier {

}
