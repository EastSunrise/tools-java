package wsg.tools.internet.info.adult.west;

import wsg.tools.internet.base.IntIdentifier;

/**
 * A member on the site.
 *
 * @author Kingen
 * @see BabesVideo#getAuthor()
 * @since 2021/4/3
 */
public class BabesMember implements IntIdentifier {

    private final int id;
    private final String name;

    BabesMember(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
