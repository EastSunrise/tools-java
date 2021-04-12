package wsg.tools.internet.movie.resource;

import wsg.tools.common.util.function.TitleSupplier;
import wsg.tools.internet.base.view.PathSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;

/**
 * An index pointing to a {@link GrapeVodItem} in the {@link GrapeSite}.
 *
 * @author Kingen
 * @see GrapeSite#findPage(GrapeVodType, GrapeVodPageReq)
 * @since 2021/3/9
 */
public interface GrapeVodIndex extends PathSupplier, TitleSupplier, UpdateDatetimeSupplier {

    /**
     * Returns the state of the item.
     *
     * @return the state
     */
    String getState();
}
