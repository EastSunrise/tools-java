package wsg.tools.common.net;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Objects;

/**
 * Utility for {@link java.net}
 *
 * @author Kingen
 * @since 2021/3/25
 */
public final class NetUtils {

    private NetUtils() {
    }

    /**
     * Creates a {@link URL}.
     *
     * @param url the string of the url
     * @return a url
     * @throws NullPointerException     if the string of url is null
     * @throws IllegalArgumentException if an exception is thrown
     * @see URL#URL(String)
     */
    public static URL createURL(String url) {
        Objects.requireNonNull(url, "the string of the url");
        try {
            return new URL(url);
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

    /**
     * Transforms a {@link URI} to a {@link URL}.
     *
     * @param uri the uri to be transformed
     * @return a url
     * @throws IllegalArgumentException if an exception is thrown
     * @see URI#toURL()
     */
    public static URL toURL(URI uri) {
        if (uri == null) {
            return null;
        }
        try {
            return uri.toURL();
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }
}
