package wsg.tools.internet.common;

import java.io.IOException;
import java.nio.charset.Charset;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import wsg.tools.common.constant.Constants;

/**
 * An implementation of {@link wsg.tools.internet.base.WrappedResponseHandler} that assumes the
 * content of the response is a string and then returns the string and the headers.
 *
 * @author Kingen
 * @since 2021/4/27
 */
public class WrappedStringResponseHandler extends AbstractWrappedResponseHandler<String> {

    private final Charset charset;

    public WrappedStringResponseHandler(Charset charset) {
        this.charset = charset;
    }

    public WrappedStringResponseHandler() {
        this.charset = Constants.UTF_8;
    }

    @Override
    public String handleEntity(HttpEntity entity) throws IOException {
        return EntityUtils.toString(entity, charset);
    }
}
