package wsg.tools.internet.base.support;

import java.io.IOException;
import javax.annotation.Nonnull;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;
import wsg.tools.internet.base.ResponseWrapper;
import wsg.tools.internet.base.WrappedResponseHandler;

/**
 * This class provides skeletal implementation of {@link WrappedResponseHandler}.
 *
 * @author Kingen
 * @see org.apache.http.impl.client.AbstractResponseHandler
 * @since 2021/4/9
 */
public abstract class AbstractWrappedResponseHandler<T> implements WrappedResponseHandler<T> {

    @Override
    public ResponseWrapper<T> handleResponse(@Nonnull HttpResponse response) throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                statusLine.getReasonPhrase());
        }
        Header[] headers = response.getAllHeaders();
        if (entity == null) {
            return new BasicResponseWrapper<>(headers, null);
        }
        return new BasicResponseWrapper<>(headers, handleEntity(entity));
    }

    /**
     * Handles the response entity and transforms it into the actual response object.
     *
     * @param entity the entity to be handled
     * @return the actual object
     * @throws IOException if an I/O exception occurs when handling the entity
     */
    public abstract T handleEntity(HttpEntity entity) throws IOException;
}
