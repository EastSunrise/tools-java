package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.common.constant.SignEnum;
import wsg.tools.internet.resource.entity.resource.base.FilenameSupplier;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Resources of http/https/ftp, except {@link BaiduDiskResource} and {@link UcDiskResource}.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class HttpResource extends ValidResource implements FilenameSupplier {

    public static final String[] PERMIT_SCHEMES = new String[]{"http://", "https://", "ftp://"};

    private final URL url;

    protected HttpResource(String title, URL url) {
        super(title);
        this.url = url;
    }

    public static HttpResource of(String title, String url) throws InvalidResourceException {
        try {
            url = decode(url);
            return new HttpResource(title, new URL(url));
        } catch (MalformedURLException e) {
            throw new InvalidResourceException("Not a valid http url: " + e.getMessage(), title, url);
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
