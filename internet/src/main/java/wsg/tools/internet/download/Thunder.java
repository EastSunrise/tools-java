package wsg.tools.internet.download;

import java.nio.charset.Charset;
import java.util.Base64;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.Constants;
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
        String decoded = url.strip();
        while (StringUtils.startsWithIgnoreCase(decoded, THUNDER_PREFIX)) {
            decoded = StringUtils.replace(decoded, "%2b", "+");
            decoded = StringUtils.replace(decoded, "%20", "+");
            decoded = StringUtils.replace(decoded, "%3D", "=");
            decoded = RegexUtils.matchesOrElseThrow(THUNDER_REGEX, decoded).group("c");
            decoded = new String(Base64.getDecoder().decode(decoded.getBytes(charset)), charset);
            decoded = RegexUtils.matchesOrElseThrow(SRC_URL_REGEX, decoded).group("u").strip();
        }
        return decoded;
    }
}
