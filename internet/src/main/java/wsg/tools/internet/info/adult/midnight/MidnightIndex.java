package wsg.tools.internet.info.adult.midnight;

import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.TitleSupplier;

/**
 * An index pointing to a {@link BaseMidnightItem} in the {@link MidnightSite}.
 *
 * @author Kingen
 * @see MidnightSite#findPage(MidnightPageReq)
 * @since 2021/3/8
 */
public class MidnightIndex implements IntIdentifier, TitleSupplier {

    private final int id;
    private final String title;

    MidnightIndex(int id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
