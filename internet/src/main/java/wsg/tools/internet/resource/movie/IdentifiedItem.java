package wsg.tools.internet.resource.movie;

import wsg.tools.common.lang.IntIdentifier;

import javax.annotation.Nonnull;

/**
 * Item with an identifier.
 *
 * @author Kingen
 * @since 2021/1/10
 */
public class IdentifiedItem extends BasicItem implements IntIdentifier {

    private final int id;
    private final String url;

    public IdentifiedItem(int id, @Nonnull String url) {
        this.id = id;
        this.url = url;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }
}