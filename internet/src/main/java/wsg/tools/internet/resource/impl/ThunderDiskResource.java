package wsg.tools.internet.resource.impl;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.resource.base.*;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of thunder disk.
 *
 * @author Kingen
 * @since 2021/2/6
 */
public class ThunderDiskResource extends AbstractResource implements PasswordProvider {

    public static final String THUNDER_DISK_HOST = "pan.xunlei.com";
    private static final Pattern PASSWORD_REGEX = Pattern.compile("[a-z0-9]{4}");
    private static final Pattern URL_REGEX = Pattern.compile("https://pan\\.xunlei\\.com/s/(?<key>[\\w-]{26})");

    private final String key;
    private final String password;

    private ThunderDiskResource(String title, String key, String password) {
        super(title);
        this.key = key;
        this.password = Objects.requireNonNull(password);
    }

    public static ThunderDiskResource of(String title, String url, String password) throws InvalidResourceException {
        if (!StringUtils.containsIgnoreCase(url, THUNDER_DISK_HOST)) {
            throw new UnknownResourceException(ThunderDiskResource.class, title, url);
        }
        if (password == null) {
            throw new InvalidPasswordException(ThunderDiskResource.class, title, url);
        }
        if (!PASSWORD_REGEX.matcher(password).matches()) {
            throw new InvalidPasswordException(ThunderDiskResource.class, title, url, password);
        }

        Matcher matcher = URL_REGEX.matcher(url);
        if (matcher.matches()) {
            return new ThunderDiskResource(title, matcher.group("key"), password);
        }
        throw new InvalidResourceException(ThunderDiskResource.class, title, url);
    }

    @Override
    public String getUrl() {
        return String.format("https://pan.xunlei.com/s/%s", key);
    }

    @Override
    public String getPassword() {
        return password;
    }
}
