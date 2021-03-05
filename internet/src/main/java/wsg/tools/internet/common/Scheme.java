package wsg.tools.internet.common;

/**
 * Scheme of the website.
 *
 * @author Kingen
 * @since 2020/6/15
 */
public enum Scheme {
    /**
     * Protocol of the url
     */
    HTTPS, HTTP, FTP;

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
