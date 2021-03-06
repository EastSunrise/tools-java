package wsg.tools.internet.download.support;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.download.view.PasswordProvider;

/**
 * A link of baidu disk.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public final class BaiduDiskLink extends AbstractLink implements PasswordProvider {

    public static final String BAIDU_DISK_HOST = "pan.baidu.com";

    private static final Pattern PASSWORD_REGEX = Pattern.compile("\\w{4}");

    private static final Pattern URI_REGEX_S =
        Pattern.compile(
            "(https?://)?pan\\.baidu\\.com(/s/1|/share/init\\?surl=)(?<s>[\\w-]{22}|\\w{4,7})(?![\\w-])");

    private static final Pattern URI_REGEX_SHARE = Pattern.compile(
        "(https?://)?pan\\.baidu\\.com/share/(link|init)\\?(shareid=\\d+&uk=\\d+|uk=\\d+&shareid=\\d+)(?!\\d)");

    private final String url;

    private final String password;

    private BaiduDiskLink(String title, String url, String password) {
        super(title);
        this.url = url;
        this.password = password;
    }

    @Nonnull
    @Contract("_, _, _ -> new")
    public static BaiduDiskLink of(String title, String url, String password)
        throws InvalidResourceException {
        if (!StringUtils.containsIgnoreCase(url, BAIDU_DISK_HOST)) {
            throw new UnknownResourceException(BaiduDiskLink.class, title, url);
        }
        if (password != null && !PASSWORD_REGEX.matcher(password).matches()) {
            throw new InvalidPasswordException(BaiduDiskLink.class, title, url, password);
        }

        Matcher sMatcher = URI_REGEX_S.matcher(url);
        if (sMatcher.find()) {
            return new BaiduDiskLink(title,
                String.format("https://pan.baidu.com/share/init?surl=%s", sMatcher.group("s")),
                password);
        }
        Matcher shareMatcher = URI_REGEX_SHARE.matcher(url);
        if (shareMatcher.find()) {
            return new BaiduDiskLink(title, shareMatcher.group(), password);
        }
        throw new InvalidResourceException(BaiduDiskLink.class, title, url);
    }

    @Nonnull
    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
