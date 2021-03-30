package wsg.tools.internet.resource.movie;

import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.IntIdentifier;
import wsg.tools.internet.base.SubtypeSupplier;
import wsg.tools.internet.resource.common.CoverSupplier;

/**
 * An item with an integer identifier and a cover that also can be classified to a subtype.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public abstract class BaseIdentifiedItem extends BasicItem
    implements IntIdentifier, SubtypeSupplier, CoverSupplier {

    private final int id;
    private final String url;

    BaseIdentifiedItem(int id, String url) {
        this.id = id;
        this.url = AssertUtils.requireNotBlank(url, "the url of an item");
    }

    @Override
    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
