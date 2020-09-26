package wsg.tools.internet.base;

import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;

/**
 * Response exception for 404.
 *
 * @author Kingen
 * @since 2020/9/26
 */
public class NotFoundException extends HttpResponseException {
    public NotFoundException(String reasonPhrase) {
        super(HttpStatus.SC_NOT_FOUND, reasonPhrase);
    }
}
