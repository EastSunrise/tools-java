package wsg.tools.internet.base;

import org.apache.http.client.HttpResponseException;

/**
 * Handle content of the response.
 *
 * @param <T> type of target object
 * @author Kingen
 * @since 2021/2/11
 */
public interface ContentHandler<T> {

    /**
     * Transfers the content to target object.
     *
     * @param content content to transfer
     * @return target object
     * @throws HttpResponseException if an error occurs
     */
    T handleContent(String content) throws HttpResponseException;

    /**
     * Obtains suffix of the target type.
     *
     * @return suffix
     */
    String suffix();
}
