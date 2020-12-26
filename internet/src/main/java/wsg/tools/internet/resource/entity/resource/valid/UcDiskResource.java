package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.internet.resource.entity.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of UC disk.
 *
 * @author Kingen
 * @since 2020/10/30
 */
public class UcDiskResource extends ValidResource {

    public static final String HOST = "yun.cn";

    private static final Pattern URL_REGEX = Pattern.compile("https://www\\.yun\\.cn/s/(?<key>[0-9a-z]{32})(\\?chkey=(?<ck>[0-9a-z]{5}))?(#/list/share)?");

    private final String key;
    private final String ck;

    private UcDiskResource(String title, String key, String ck) {
        super(title);
        this.key = Objects.requireNonNull(key);
        this.ck = ck;
    }

    public static UcDiskResource of(String title, String url) throws InvalidResourceException {
        Matcher matcher = URL_REGEX.matcher(url);
        if (matcher.matches()) {
            return new UcDiskResource(title, matcher.group("key"), matcher.group("ck"));
        }
        throw new InvalidResourceException("Not a valid UC Disk url.", title, url);
    }

    @Override
    public String getUrl() {
        String url = String.format("https://www.yun.cn/s/%s", key);
        if (ck != null) {
            url += "?chkey=" + ck;
        }
        return url;
    }
}
