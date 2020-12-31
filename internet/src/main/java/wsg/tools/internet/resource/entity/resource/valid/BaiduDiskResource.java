package wsg.tools.internet.resource.entity.resource.valid;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.PasswordProvider;
import wsg.tools.internet.resource.entity.resource.base.UnknownResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of baidu disk.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class BaiduDiskResource extends ValidResource implements PasswordProvider {

    public static final String BAIDU_DISK_HOST = "pan.baidu.com";

    private static final Pattern PASSWORD_REGEX = Pattern.compile("[A-Za-z0-9]{4}");
    private static final Pattern URI_REGEX_S = Pattern.compile("(https?://)?pan\\.baidu\\.com(/s/1|/share/init\\?surl=)(?<s>[\\w-]{22}|\\w{4,7})(?![\\w-])");
    private static final Pattern URI_REGEX_SHARE = Pattern.compile("(https?://)?pan\\.baidu\\.com/share/(link|init)\\?(shareid=\\d+&uk=\\d+|uk=\\d+&shareid=\\d+)(?!\\d)");

    private final String url;
    private final String password;

    private BaiduDiskResource(String title, String url, String password) {
        super(title);
        this.url = url;
        this.password = password;
    }

    public static BaiduDiskResource of(String title, String url, String password) throws InvalidResourceException {
        if (!StringUtils.containsIgnoreCase(url, BAIDU_DISK_HOST)) {
            throw new UnknownResourceException("Not a Baidu disk url", title, url);
        }
        if (password != null && !PASSWORD_REGEX.matcher(password).matches()) {
            throw new InvalidResourceException("Not a valid password of Baidu disk", title, url, password);
        }

        Matcher matcher = URI_REGEX_S.matcher(url);
        if (matcher.find()) {
            return new BaiduDiskResource(title, String.format("https://pan.baidu.com/share/init?surl=%s", matcher.group("s")), password);
        }
        matcher = URI_REGEX_SHARE.matcher(url);
        if (matcher.find()) {
            return new BaiduDiskResource(title, matcher.group(), password);
        }
        throw new InvalidResourceException("Not a valid Baidu disk url", title, url, password);
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
