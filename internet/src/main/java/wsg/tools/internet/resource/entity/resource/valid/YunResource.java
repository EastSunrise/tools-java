package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of UC disk.
 *
 * @author Kingen
 * @since 2020/10/30
 */
public class YunResource extends BaseValidResource {

    public static final String YUN_HOST = "yun.cn";
    private static final Pattern URL_REGEX = Pattern.compile("https://www\\.yun\\.cn/s/(?<key>[0-9a-z]{32})(\\?chkey=4e2xc)?");

    private final String key;

    public YunResource(String title, String key) {
        super(title);
        this.key = key;
    }

    public static YunResource of(String title, String url) {
        Matcher matcher = URL_REGEX.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Not a valid yun url.");
        }
        return new YunResource(title, matcher.group("key"));
    }

    @Override
    public String getUrl() {
        return String.format("https://www.yun.cn/s/%s", key);
    }
}
