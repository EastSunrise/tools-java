package wsg.tools.internet.base;

import org.apache.http.Header;
import wsg.tools.internet.base.support.BaseSite;

/**
 * An object returned by {@link BaseSite#getResponseWrapper}, including the headers of the response
 * and the content of the response.
 *
 * @param <T> real type of the content of the response
 * @author Kingen
 * @since 2021/4/9
 */
public interface ResponseWrapper<T> {

    /**
     * Returns the headers of the response.
     *
     * @return the headers
     */
    Header[] getHeaders();

    /**
     * Returns the content of the response as an object.
     *
     * @return the content of the response
     */
    T getContent();
}
