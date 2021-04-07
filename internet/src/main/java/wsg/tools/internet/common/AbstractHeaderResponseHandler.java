package wsg.tools.internet.common;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;

/**
 * A generic {@link ResponseHandler} that returns the response entity and headers for successful
 * (2xx) responses, or throws an exception if the response code was &gt;= 300.
 *
 * @author Kingen
 * @see org.apache.http.impl.client.AbstractResponseHandler
 * @since 2021/4/7
 */
public abstract class AbstractHeaderResponseHandler<T>
    implements ResponseHandler<Pair<Map<String, List<Header>>, T>> {

    @Override
    public Pair<Map<String, List<Header>>, T> handleResponse(@Nonnull HttpResponse response)
        throws IOException {
        StatusLine statusLine = response.getStatusLine();
        HttpEntity entity = response.getEntity();
        if (statusLine.getStatusCode() >= 300) {
            EntityUtils.consume(entity);
            throw new HttpResponseException(statusLine.getStatusCode(),
                statusLine.getReasonPhrase());
        }
        Map<String, List<Header>> headers = Arrays.stream(response.getAllHeaders())
            .collect(Collectors.groupingBy(Header::getName));
        if (entity == null) {
            return Pair.of(headers, null);
        }
        return Pair.of(headers, handleEntity(entity));
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
