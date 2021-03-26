package wsg.tools.internet.common;

import java.util.Locale;

/**
 * Protocols of urls.
 *
 * @author Kingen
 * @since 2020/6/15
 */
public enum Scheme {
    /**
     * Protocols of the url
     */
    HTTPS, HTTP, FTP;

    @Override
    public String toString() {
        return name().toLowerCase(Locale.ENGLISH);
    }
}
