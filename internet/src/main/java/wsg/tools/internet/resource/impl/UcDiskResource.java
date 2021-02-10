package wsg.tools.internet.resource.impl;

import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.InvalidResourceException;
import wsg.tools.internet.resource.base.UnknownResourceException;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of UC disk.
 *
 * @author Kingen
 * @since 2020/10/30
 */
public class UcDiskResource extends AbstractResource {

    public static final String UC_DISK_HOST = "yun.cn";

    private static final Pattern UC_REGEX = Pattern.compile("https://www\\.yun\\.cn/s/(?<key>[0-9a-z]{32})(\\?chkey=(?<ck>[0-9a-z]{5}))?");

    private final String key;
    private final String ck;

    private UcDiskResource(String title, String key, String ck) {
        super(title);
        this.key = Objects.requireNonNull(key);
        this.ck = ck;
    }

    public static UcDiskResource of(String title, String url) throws InvalidResourceException {
        if (!StringUtils.containsIgnoreCase(url, UC_DISK_HOST)) {
            throw new UnknownResourceException(UcDiskResource.class, title, url);
        }
        Matcher matcher = UC_REGEX.matcher(url);
        if (matcher.lookingAt()) {
            return new UcDiskResource(title, matcher.group("key"), matcher.group("ck"));
        }
        throw new InvalidResourceException(UcDiskResource.class, title, url);
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
