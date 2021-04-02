package wsg.tools.internet.download;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.regex.RegexUtils;

/**
 * Utility for thunder.
 *
 * @author Kingen
 * @see <a href="https://www.xunlei.com/">Thunder</a>
 * @since 2020/10/8
 */
public final class Thunder {

    public static final String THUNDER_PREFIX = "thunder://";
    public static final String EMPTY_LINK = "thunder://QUFaWg==";
    private static final Pattern THUNDER_REGEX = Pattern
        .compile("thunder://(?<c>([\\w+/-]{4})+([\\w+/-]{2}[\\w+/-=]=)?)",
            Pattern.CASE_INSENSITIVE);
    private static final Pattern SRC_URL_REGEX = Pattern.compile("AA\\s*(?<u>.*)\\s*ZZ");

    private Thunder() {
    }

    /**
     * Encode a url to a thunder url.
     */
    public static String encodeThunder(@Nonnull String url) {
        if (StringUtils.startsWithIgnoreCase(url, THUNDER_PREFIX)) {
            return url;
        }
        byte[] bytes = ("AA" + url + "ZZ").getBytes(Constants.UTF_8);
        return THUNDER_PREFIX + Base64.getEncoder().encodeToString(bytes);
    }

    /**
     * Decode a thunder to a common url.
     */
    public static String decodeThunder(@Nonnull String url, Charset charset) {
        url = url.strip();
        while (StringUtils.startsWithIgnoreCase(url, THUNDER_PREFIX)) {
            url = StringUtils.replace(url, "%2b", "+");
            url = StringUtils.replace(url, "%20", "+");
            url = StringUtils.replace(url, "%3D", "=");
            url = RegexUtils.matchesOrElseThrow(THUNDER_REGEX, url).group("c");
            url = new String(Base64.getDecoder().decode(url.getBytes(charset)), charset);
            url = RegexUtils.matchesOrElseThrow(SRC_URL_REGEX, url).group("u").strip();
        }
        return url;
    }
}
