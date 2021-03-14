package wsg.tools.internet.download;

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
import wsg.tools.common.constant.Constants;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.impl.BaiduDiskLink;
import wsg.tools.internet.download.impl.Ed2kLink;
import wsg.tools.internet.download.impl.HttpLink;
import wsg.tools.internet.download.impl.MagnetLink;
import wsg.tools.internet.download.impl.Thunder;
import wsg.tools.internet.download.impl.ThunderDiskLink;
import wsg.tools.internet.download.impl.UcDiskLink;
import wsg.tools.internet.download.impl.YyetsLink;

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

    public static AbstractLink create(String title, String url) throws InvalidResourceException {
        return create(title, url, Constants.UTF_8);
    }

    public static AbstractLink create(String title, String url, @Nonnull Charset charset)
        throws InvalidResourceException {
        return create(title, url, charset, null);
    }

    public static AbstractLink create(String title, String url,
        @Nullable Functions.FailableSupplier<String, InvalidPasswordException> passwordProvider)
        throws InvalidResourceException {
        return create(title, url, Constants.UTF_8, passwordProvider);
    }

    /**
     * Create a resource based on the given url and title.
     */
    public static AbstractLink create(String title, String url, @Nonnull Charset charset,
        @Nullable Functions.FailableSupplier<String, InvalidPasswordException> passwordProvider)
        throws InvalidResourceException {
        Objects.requireNonNull(url);
        if (StringUtils.startsWithIgnoreCase(url, Thunder.THUNDER_PREFIX)) {
            try {
                url = Thunder.decodeThunder(url, charset);
            } catch (IllegalArgumentException e) {
                throw new InvalidResourceException("Not a valid thunder url", title, url);
            }
        }
        try {
            url = URLDecoder.decode(url, charset);
        } catch (IllegalArgumentException ignored) {
        }

        if (StringUtils.startsWithIgnoreCase(url, Ed2kLink.ED2K_PREFIX)) {
            return Ed2kLink.of(title, url);
        }
        if (StringUtils.startsWithIgnoreCase(url, MagnetLink.MAGNET_PREFIX)) {
            return MagnetLink.of(title, url);
        }
        if (StringUtils.startsWithIgnoreCase(url, YyetsLink.YYETS_PREFIX)) {
            return YyetsLink.of(title, url);
        }
        if (StringUtils.containsIgnoreCase(url, BaiduDiskLink.BAIDU_DISK_HOST)) {
            if (passwordProvider != null) {
                return BaiduDiskLink.of(title, url, passwordProvider.get());
            }
            throw new InvalidPasswordException(BaiduDiskLink.class, title, url);
        }
        if (StringUtils.containsIgnoreCase(url, ThunderDiskLink.THUNDER_DISK_HOST)) {
            if (passwordProvider != null) {
                return ThunderDiskLink.of(title, url, passwordProvider.get());
            }
            throw new InvalidPasswordException(ThunderDiskLink.class, title, url);
        }
        if (StringUtils.containsIgnoreCase(url, UcDiskLink.UC_DISK_HOST)) {
            return UcDiskLink.of(title, url);
        }
        for (String prefix : HttpLink.HTTP_PREFIXES) {
            if (StringUtils.startsWithIgnoreCase(url, prefix)) {
                return HttpLink.of(title, url);
            }
        }
        throw new UnknownResourceException("Unknown type of resource", title, url);
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
