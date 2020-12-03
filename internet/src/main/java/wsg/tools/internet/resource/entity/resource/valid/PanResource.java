package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of baidu disk.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class PanResource extends BaseValidResource {

    public static final String PAN_HOST = "pan.baidu.com";
    private static final Pattern URI_REGEX_S = Pattern.compile(
            "https?://pan\\.baidu\\.com/s/1(?<surl>[0-9A-z]{4,7}|[\\w-]{22})(\\?fid=\\d+|&shfl=sharep?set|[^\\w-].*)?");
    private static final Pattern URI_REGEX_SHARE = Pattern.compile(
            "https?://pan\\.baidu\\.com/share/init\\?surl=(?<surl>[0-9A-z]{4,7}|[\\w-]{22})");

    private final String surl;

    private PanResource(String title, String surl) {
        super(title);
        this.surl = surl;
    }

    public static PanResource of(String title, String url) {
        Matcher matcher = URI_REGEX_S.matcher(url);
        if (matcher.matches()) {
            return new PanResource(title, matcher.group("surl"));
        }
        matcher = URI_REGEX_SHARE.matcher(url);
        if (matcher.matches()) {
            return new PanResource(title, matcher.group("surl"));
        }
        throw new IllegalArgumentException("Not a valid pan url.");
    }

    @Override
    public String getUrl() {
        return String.format("https://pan.baidu.com/share/init?surl=%s", surl);
    }
}
