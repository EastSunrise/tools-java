package wsg.tools.internet.info.adult.west;

import java.io.Serializable;
import wsg.tools.internet.base.view.IntIdentifier;

/**
 * A member on the site.
 *
 * @author Kingen
 * @see BabesVideo#getAuthor()
 * @since 2021/4/3
 */
public class BabesMember implements IntIdentifier, Serializable {

    private static final long serialVersionUID = -1317071839092458415L;

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
