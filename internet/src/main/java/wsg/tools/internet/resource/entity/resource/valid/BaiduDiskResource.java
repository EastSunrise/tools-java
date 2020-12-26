package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.PasswordProvider;
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

    public static final String HOST = "pan.baidu.com";

    private static final Pattern LINK_AND_CODE_REGEX =
            Pattern.compile("(链接|百度云)(：|:|: )(?<u>https://pan\\.baidu\\.com/[\\w-/?=&]+) *((提取码|密码)(：|: )((?<p>[\\w]{4})( \\W.*)?)?)?");
    private static final Pattern PASSWORD_REGEX = Pattern.compile("(密码：)?(?<p>[\\w]{4})\\W*");
    private static final Pattern URI_REGEX_S = Pattern.compile("(https?://)?pan\\.baidu\\.com/s/1(?<s>[0-9A-z]{4,7}|[\\w-]{22})(\\?fid=\\d+|&shfl=sharep?set|[^\\w-].*)?");
    private static final Pattern URI_REGEX_SHARE = Pattern.compile("https?://pan\\.baidu\\.com/share/init\\?surl=(?<s>[0-9A-z]{4,7}|[\\w-]{22})");
    private static final Pattern URI_REGEX_SHARE_LINK = Pattern.compile("(?<u>https?://pan\\.baidu\\.com/share/(link|init)\\?shareid=\\d+&uk=\\d+(&fid=\\d+)?)(&.*|#.*)?");

    private final String url;
    private final String password;

    private BaiduDiskResource(String title, String url, String password) {
        super(title);
        this.url = url;
        this.password = password;
    }

    public static BaiduDiskResource of(String title, String url, String password) throws InvalidResourceException {
        Matcher matcher = LINK_AND_CODE_REGEX.matcher(url);
        if (matcher.matches()) {
            url = matcher.group("u");
            String p = matcher.group("p");
            if (p != null) {
                password = p;
            }
        }
        if (password != null) {
            password = password.strip();
            Matcher m = PASSWORD_REGEX.matcher(password);
            if (!m.matches()) {
                throw new InvalidResourceException("Not a valid password of Baidu Disk url.", title, url, password);
            }
            password = m.group("p");
        }

        matcher = URI_REGEX_S.matcher(url);
        if (matcher.matches()) {
            return new BaiduDiskResource(title, String.format("https://pan.baidu.com/share/init?surl=%s", matcher.group("s")), password);
        }
        matcher = URI_REGEX_SHARE.matcher(url);
        if (matcher.matches()) {
            return new BaiduDiskResource(title, String.format("https://pan.baidu.com/share/init?surl=%s", matcher.group("s")), password);
        }
        matcher = URI_REGEX_SHARE_LINK.matcher(url);
        if (matcher.matches()) {
            return new BaiduDiskResource(title, matcher.group("u"), password);
        }
        throw new InvalidResourceException("Not a valid Baidu Disk url.", title, url);
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
