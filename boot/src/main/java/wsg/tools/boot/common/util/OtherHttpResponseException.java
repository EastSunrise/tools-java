package wsg.tools.boot.common.util;

import lombok.Getter;
import org.apache.http.client.HttpResponseException;

/**
 * Thrown when catching a {@link org.apache.http.client.HttpResponseException} except {@link
 * wsg.tools.boot.common.NotFoundException}.
 *
 * @author Kingen
 * @see SiteUtilExt#ifNotFound
 * @see SiteUtilExt#found
 * @since 2021/3/9
 */
@Getter
public class OtherHttpResponseException extends Exception {

    private static final long serialVersionUID = -5638361744618529498L;

    private final HttpResponseException exception;

    OtherHttpResponseException(HttpResponseException exception) {
        this.exception = exception;
    }
}
