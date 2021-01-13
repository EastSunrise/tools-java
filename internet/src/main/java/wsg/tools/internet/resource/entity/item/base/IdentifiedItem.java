package wsg.tools.internet.resource.entity.item.base;

import wsg.tools.common.lang.Identifier;

import javax.annotation.Nonnull;

/**
 * Item with an identifier.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class IdentifiedItem extends BaseItem implements Identifier<Integer> {

    private final int id;
    private final String url;

    public IdentifiedItem(int id, @Nonnull String url) {
        this.id = id;
        this.url = url;
    }

    @Override
    public Integer getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}
