package wsg.tools.internet.download;

import javax.annotation.Nonnull;

/**
 * Represents a link that points to a resource on the Internet.
 *
 * @author Kingen
 * @since 2021/4/3
 */
public interface Link {

    /**
     * Returns the url of this link.
     *
     * @return the url
     */
    @Nonnull
    String getUrl();

    /**
     * Returns the title of the resource.
     *
     * @return the title
     */
    String getTitle();
}
