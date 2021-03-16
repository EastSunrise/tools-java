package wsg.tools.internet.common;

import org.apache.http.client.HttpResponseException;

/**
 * Thrown when catching a {@link HttpResponseException} whose {@link #getStatusCode()} isn't 404.
 *
 * @author Kingen
 * @since 2021/3/16
 */
public class OtherHttpResponseException extends HttpResponseException {

    private static final long serialVersionUID = -525774900924868242L;

    public OtherHttpResponseException(HttpResponseException e) {
        super(e.getStatusCode(), e.getReasonPhrase());
    }

    public OtherHttpResponseException(int statusCode, String reasonPhrase) {
        super(statusCode, reasonPhrase);
    }
}
