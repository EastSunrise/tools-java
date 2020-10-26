package wsg.tools.internet.resource.entity.resource;

import lombok.Getter;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;

import javax.annotation.Nonnull;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of ed2k.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class Ed2kResource extends AbstractResource {

    public static final String SCHEME = "ed2k";

    private static final Pattern URI_REGEX = Pattern.compile(
            "ed2k://\\|file" +
                    "\\|(?<name>[^|]+)" +
                    "\\|(?<size>\\d+)" +
                    "\\|(?<hash>[0-9A-z]{32})" +
                    "(\\|h=(?<h>[0-9A-z]{32}))?" +
                    "(\\|/)?(?<extra>.*)");
    private final String filename;
    private final long size;
    private final String hash;
    /**
     * may null
     */
    private final String rootHash;
    private final String extra;

    public Ed2kResource(String url) {
        url = URLDecoder.decode(url, Constants.UTF_8);
        Matcher matcher = AssertUtils.matches(URI_REGEX, url);
        this.filename = matcher.group("name");
        this.size = Long.parseLong(matcher.group("size"));
        this.hash = matcher.group("hash");
        this.rootHash = matcher.group("h");
        this.extra = matcher.group("extra");
    }

    @Override
    public String getUrl() {
        StringBuilder builder = new StringBuilder("ed2k://|file");
        builder.append("|").append(filename)
                .append("|").append(size)
                .append("|").append(hash);
        if (rootHash != null) {
            builder.append("|h=").append(rootHash);
        }
        builder.append("|/");
        return builder.toString();
    }

    @Override
    @Nonnull
    public String filename() {
        return filename;
    }

    @Override
    public long size() {
        return size;
    }
}
