package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.common.lang.AssertUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of UC disk.
 *
 * @author Kingen
 * @since 2020/10/30
 */
public class YunResource extends HttpResource {

    private static final Pattern URL_REGEX = Pattern.compile("https://www\\.yun\\.cn/s/[0-9a-z]{32}(\\?chkey=4e2xc)?");

    private YunResource(String title, URL url) {
        super(title, url);
    }

    public static YunResource of(String title, String url) {
        Matcher matcher = URL_REGEX.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Not a valid yun url.");
        }
        try {
            return new YunResource(title, new URL(url));
        } catch (MalformedURLException e) {
            throw AssertUtils.runtimeException(e);
        }
    }
}
