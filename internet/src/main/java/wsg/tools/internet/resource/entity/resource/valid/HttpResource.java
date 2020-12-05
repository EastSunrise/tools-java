package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.common.constant.SignEnum;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;
import wsg.tools.internet.resource.entity.resource.base.FilenameSupplier;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resources of http/https/ftp, except {@link PanResource} and {@link YunResource}.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class HttpResource extends BaseValidResource implements FilenameSupplier {

    public static final String[] PERMIT_SCHEMES = new String[]{"http", "https", "ftp"};

    private final URL url;

    protected HttpResource(String title, URL url) {
        super(title);
        this.url = url;
    }

    public static HttpResource of(String title, String url) {
        try {
            url = decode(url);
            return new HttpResource(title, new URL(url));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Not a valid url: " + e.getMessage());
        }
    }

    @Override
    public String getUrl() {
        return this.url.toString();
    }

    @Override
    public String getFilename() {
        String path = this.url.getPath();
        if (path == null || path.endsWith(SignEnum.URL_PATH_SEPARATOR)) {
            return "index.html";
        } else {
            return path.substring(path.lastIndexOf(SignEnum.URL_PATH_SEPARATOR) + 1);
        }
    }
}
