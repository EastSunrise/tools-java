package wsg.tools.internet.resource.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.function.throwable.ThrowableSupplier;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidPasswordException;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.base.UnknownResourceException;
import wsg.tools.internet.resource.download.Thunder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Factory of resources.
 *
 * @author Kingen
 * @since 2020/10/8
 */
@Slf4j
public final class ResourceFactory {

    private static final Pattern PASSWORD_REGEX = Pattern.compile("(提取码|密码)[:：]\\s?(?<p>\\w{4})");

    public static AbstractResource create(String title, String url) throws InvalidResourceException {
        return create(title, url, Constants.UTF_8);
    }

    public static AbstractResource create(String title, String url, @Nonnull Charset charset) throws InvalidResourceException {
        return create(title, url, charset, null);
    }

    public static AbstractResource create(String title, String url, @Nullable ThrowableSupplier<String, InvalidPasswordException> passwordProvider)
            throws InvalidResourceException {
        return create(title, url, Constants.UTF_8, passwordProvider);
    }

    /**
     * Create a resource based on the given url and title.
     */
    public static AbstractResource create(String title, String url, @Nonnull Charset charset,
                                          @Nullable ThrowableSupplier<String, InvalidPasswordException> passwordProvider) throws InvalidResourceException {
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

        if (StringUtils.startsWithIgnoreCase(url, Ed2kResource.ED2K_PREFIX)) {
            return Ed2kResource.of(title, url);
        }
        if (StringUtils.startsWithIgnoreCase(url, MagnetResource.MAGNET_PREFIX)) {
            return MagnetResource.of(title, url);
        }
        if (StringUtils.startsWithIgnoreCase(url, YyetsResource.YYETS_PREFIX)) {
            return YyetsResource.of(title, url);
        }
        if (StringUtils.containsIgnoreCase(url, BaiduDiskResource.BAIDU_DISK_HOST)) {
            if (passwordProvider != null) {
                return BaiduDiskResource.of(title, url, passwordProvider.get());
            }
            throw new InvalidPasswordException(BaiduDiskResource.class, title, url);
        }
        if (StringUtils.containsIgnoreCase(url, ThunderDiskResource.THUNDER_DISK_HOST)) {
            if (passwordProvider != null) {
                return ThunderDiskResource.of(title, url, passwordProvider.get());
            }
            throw new InvalidPasswordException(ThunderDiskResource.class, title, url);
        }
        if (StringUtils.containsIgnoreCase(url, UcDiskResource.UC_DISK_HOST)) {
            return UcDiskResource.of(title, url);
        }
        for (String prefix : HttpResource.HTTP_PREFIXES) {
            if (StringUtils.startsWithIgnoreCase(url, prefix)) {
                return HttpResource.of(title, url);
            }
        }
        throw new UnknownResourceException("Unknown type of resource", title, url);
    }

    /**
     * Extract a password from the given strings.
     */
    public static String getPassword(String... strings) {
        for (String string : strings) {
            Matcher matcher = PASSWORD_REGEX.matcher(string);
            if (matcher.find()) {
                return matcher.group("p");
            }
        }
        return null;
    }
}
