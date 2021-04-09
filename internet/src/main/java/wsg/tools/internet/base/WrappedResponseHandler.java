package wsg.tools.internet.base;

import org.apache.http.client.ResponseHandler;

/**
 * A generic {@link ResponseHandler} that returns the response as a {@link ResponseWrapper}.
 *
 * @author Kingen
 * @see ResponseWrapper
 * @since 2021/4/9
 */
public interface WrappedResponseHandler<T> extends ResponseHandler<ResponseWrapper<T>> {

}
