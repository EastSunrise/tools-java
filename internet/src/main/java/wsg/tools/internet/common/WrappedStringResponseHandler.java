package wsg.tools.internet.common;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.base.support.AbstractWrappedResponseHandler;

/**
 * An implementation of {@link wsg.tools.internet.base.WrappedResponseHandler} that returns the
 * content of the response as a string.
 *
 * @author Kingen
 * @since 2021/3/11
 */
public class WrappedStringResponseHandler extends AbstractWrappedResponseHandler<String> {

    private final Charset charset;

    public WrappedStringResponseHandler() {
        this.charset = Constants.UTF_8;
    }

    public WrappedStringResponseHandler(Charset charset) {
        this.charset = charset;
    }

    @Override
    public String handleEntity(HttpEntity entity) throws IOException {
        return handleContent(EntityUtils.toString(entity, charset));
    }

    /**
     * Handles the content before returning
     *
     * @param content the content obtained from the response
     * @return handled content
     * @throws HttpResponseException if an exception is thrown
     */
    protected String handleContent(String content) throws HttpResponseException {
        return content;
    }
}
