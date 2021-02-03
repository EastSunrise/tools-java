package wsg.tools.internet.resource.entity.resource.impl;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.resource.entity.resource.base.FilenameSupplier;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.Resource;
import wsg.tools.internet.resource.entity.resource.base.UnknownResourceException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * Resources of http/https/ftp, except {@link BaiduDiskResource} and {@link UcDiskResource}.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class HttpResource extends Resource implements FilenameSupplier {

    public static final String[] HTTP_PREFIXES = new String[]{"https://", "http://", "ftp://"};

    private final URL url;

    protected HttpResource(String title, URL url) {
        super(title);
        this.url = url;
    }

    public static HttpResource of(String title, String url) throws InvalidResourceException {
        if (Arrays.stream(HTTP_PREFIXES).noneMatch(prefix -> StringUtils.startsWithIgnoreCase(url, prefix))) {
            throw new UnknownResourceException("Not a http url", title, url);
        }
        try {
            return new HttpResource(title, new URL(url));
        } catch (MalformedURLException e) {
            throw new InvalidResourceException("Not a valid http url", title, url);
        }
    }

    @Override
    public String getUrl() {
        return this.url.toString();
    }

    @Override
    public String getFilename() {
        String path = this.url.getPath();
        if (path == null || path.endsWith(Constants.URL_PATH_SEPARATOR)) {
            return "index.html";
        } else {
            return path.substring(path.lastIndexOf(Constants.URL_PATH_SEPARATOR) + 1);
        }
    }
}
