package wsg.tools.internet.download.support;

import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.Functions;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.Constants;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.Thunder;

/**
 * A factory to create links.
 *
 * @author Kingen
 * @since 2020/10/8
 */
@Slf4j
public final class LinkFactory {

    private static final Pattern PASSWORD_REGEX = Pattern.compile("(提取码|密码)[:：]\\s?(?<p>\\w{4})");

    private LinkFactory() {
    }

    public static Link create(String title, String url) throws InvalidResourceException {
        return create(title, url, Constants.UTF_8);
    }

    public static Link create(String title, String url, @Nonnull Charset charset)
        throws InvalidResourceException {
        return create(title, url, charset, null);
    }

    public static Link create(String title, String url,
        @Nullable Functions.FailableSupplier<String, InvalidPasswordException> passwordProvider)
        throws InvalidResourceException {
        return create(title, url, Constants.UTF_8, passwordProvider);
    }

    /**
     * Create a resource based on the given url and title.
     */
    public static Link create(String title, @Nonnull String url, @Nonnull Charset charset,
        @Nullable Functions.FailableSupplier<String, InvalidPasswordException> passwordProvider)
        throws InvalidResourceException {
        String decoded = decode(url, charset, title);
        if (StringUtils.startsWithIgnoreCase(decoded, Ed2kLink.ED2K_PREFIX)) {
            return Ed2kLink.of(title, decoded);
        }
        if (StringUtils.startsWithIgnoreCase(decoded, MagnetLink.MAGNET_PREFIX)) {
            return MagnetLink.of(title, decoded);
        }
        if (StringUtils.startsWithIgnoreCase(decoded, YyetsLink.YYETS_PREFIX)) {
            return YyetsLink.of(title, decoded);
        }
        if (StringUtils.containsIgnoreCase(decoded, BaiduDiskLink.BAIDU_DISK_HOST)) {
            if (null != passwordProvider) {
                return BaiduDiskLink.of(title, decoded, passwordProvider.get());
            }
            throw new InvalidPasswordException(BaiduDiskLink.class, title, decoded);
        }
        if (StringUtils.containsIgnoreCase(decoded, ThunderDiskLink.THUNDER_DISK_HOST)) {
            if (null != passwordProvider) {
                return ThunderDiskLink.of(title, decoded, passwordProvider.get());
            }
            throw new InvalidPasswordException(ThunderDiskLink.class, title, decoded);
        }
        if (StringUtils.containsIgnoreCase(decoded, UcDiskLink.UC_DISK_HOST)) {
            return UcDiskLink.of(title, decoded);
        }
        for (String prefix : HttpLink.HTTP_PREFIXES) {
            if (StringUtils.startsWithIgnoreCase(decoded, prefix)) {
                return HttpLink.of(title, decoded);
            }
        }
        throw new UnknownResourceException("Unknown type of resource", title, decoded);
    }

    private static String decode(String url, Charset charset, String title)
        throws InvalidResourceException {
        String decoded = Objects.requireNonNull(url);
        if (StringUtils.startsWithIgnoreCase(decoded, Thunder.THUNDER_PREFIX)) {
            try {
                decoded = Thunder.decodeThunder(decoded, charset);
            } catch (IllegalArgumentException e) {
                throw new InvalidResourceException("Not a valid thunder url", title, decoded);
            }
        }
        try {
            decoded = URLDecoder.decode(decoded, charset);
        } catch (IllegalArgumentException ignored) {
        }
        return decoded;
    }

    /**
     * Extract a password from the given strings.
     */
    public static String getPassword(String... strings) {
        for (String str : strings) {
            Matcher matcher = PASSWORD_REGEX.matcher(str);
            if (matcher.find()) {
                return matcher.group("p");
            }
        }
        return null;
    }
}
