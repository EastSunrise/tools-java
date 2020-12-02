package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.common.lang.AssertUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of baidu disk.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class PanResource extends HttpResource {

    private static final Pattern URI_REGEX_S = Pattern.compile(
            "https?://pan\\.baidu\\.com/s/1(?<surl>[0-9A-z]{4,7}|[\\w-]{22})(\\?fid=\\d+|&shfl=sharep?set|[^\\w-].*)?"
    );
    private static final Pattern URI_REGEX_SHARE = Pattern.compile(
            "https?://pan\\.baidu\\.com(?<path>/share/" +
                    "(init\\?surl=(?<surl>[0-9A-z]{4,7}|[\\w-]{22})|(link|init)\\?shareid=\\d+&uk=\\d+(&fid=\\d+)?))");

    private PanResource(String title, URL url) {
        super(title, url);
    }

    public static PanResource of(String title, String url) {
        Matcher matcher = URI_REGEX_S.matcher(url);
        if (matcher.matches()) {
            return new PanResource(title, ofPath("/share/init?surl=" + matcher.group("surl")));
        }
        matcher = URI_REGEX_SHARE.matcher(url);
        if (matcher.matches()) {
            return new PanResource(title, ofPath(matcher.group("path")));
        }
        throw new IllegalArgumentException("Not a valid pan url.");
    }

    private static URL ofPath(String path) {
        try {
            return new URL("https://pan.baidu.com" + path);
        } catch (MalformedURLException e) {
            throw AssertUtils.runtimeException(e);
        }
    }
}
