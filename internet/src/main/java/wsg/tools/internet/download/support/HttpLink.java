package wsg.tools.internet.download.support;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import javax.annotation.Nonnull;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.download.view.FilenameSupplier;

/**
 * A link of http/https/ftp, except {@link BaiduDiskLink}, {@link UcDiskLink} or {@link
 * ThunderDiskLink}.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public final class HttpLink extends AbstractLink implements FilenameSupplier {

    public static final String[] HTTP_PREFIXES = {"https://", "http://", "ftp://"};

    private final URL url;

    private HttpLink(String title, URL url) {
        super(title);
        this.url = url;
    }

    public static HttpLink of(String title, String url) throws InvalidResourceException {
        if (Arrays.stream(HTTP_PREFIXES)
            .noneMatch(prefix -> StringUtils.startsWithIgnoreCase(url, prefix))) {
            throw new UnknownResourceException(HttpLink.class, title, url);
        }
        try {
            return new HttpLink(title, new URL(url));
        } catch (MalformedURLException e) {
            throw new InvalidResourceException(HttpLink.class, title, url);
        }
    }

    @Nonnull
    @Override
    public String getUrl() {
        return url.toString();
    }

    @Override
    public String getFilename() {
        return AssertUtils.requireNotBlankElse(FilenameUtils.getName(url.getPath()), "index.html");
    }
}
