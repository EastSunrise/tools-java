package wsg.tools.internet.download.impl;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.UnknownResourceException;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.base.FilenameSupplier;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

/**
 * A link of http/https/ftp, except {@link BaiduDiskLink}, {@link UcDiskLink} or {@link ThunderDiskLink}.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class HttpLink extends AbstractLink implements FilenameSupplier {

    public static final String[] HTTP_PREFIXES = new String[]{"https://", "http://", "ftp://"};

    private final URL url;

    protected HttpLink(String title, URL url) {
        super(title);
        this.url = url;
    }

    public static HttpLink of(String title, String url) throws InvalidResourceException {
        if (Arrays.stream(HTTP_PREFIXES).noneMatch(prefix -> StringUtils.startsWithIgnoreCase(url, prefix))) {
            throw new UnknownResourceException(HttpLink.class, title, url);
        }
        try {
            return new HttpLink(title, new URL(url));
        } catch (MalformedURLException e) {
            throw new InvalidResourceException(HttpLink.class, title, url);
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
