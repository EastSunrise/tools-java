package wsg.tools.internet.download.support;

import wsg.tools.internet.download.Link;

/**
 * An abstract link pointing to a resource.
 *
 * @author Kingen
 * @since 2020/11/1
 */
abstract class AbstractLink implements Link {

    private final String title;

    AbstractLink(String title) {
        this.title = title;
    }

    @Override
    public String getTitle() {
        return title;
    }
}
