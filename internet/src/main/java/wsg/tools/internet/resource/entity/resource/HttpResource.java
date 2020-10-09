package wsg.tools.internet.resource.entity.resource;

import lombok.Getter;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignEnum;

import javax.annotation.Nonnull;
import java.net.URI;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Resources of http/https/ftp.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class HttpResource extends AbstractResource {

    public static final String[] PERMIT_SCHEMES = new String[]{"http", "https", "ftp"};

    private final URI uri;
    private final String filename;

    public HttpResource(String url) {
        url = URLEncoder.encode(url, Constants.UTF_8);
        this.uri = URI.create(url);
        String path = this.uri.getPath();
        if (path == null || path.endsWith(SignEnum.SLASH.toString())) {
            this.filename = "index.html";
        } else {
            this.filename = URLDecoder.decode(path.substring(path.lastIndexOf(SignEnum.SLASH.getC()) + 1), Constants.UTF_8);
        }
    }

    @Override
    public String getUrl() {
        return uri.toString();
    }

    @Override
    @Nonnull
    public String filename() {
        return filename;
    }

    @Override
    public long size() {
        return -1;
    }
}
