package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.internet.resource.entity.resource.base.BaseResource;

import javax.annotation.Nonnull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of ed2k.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class Ed2kResource extends BaseResource {

    public static final String SCHEME = "ed2k";

    private static final Pattern URI_REGEX = Pattern.compile(
            "ed2k://\\|file\\|+(?<name>[^|]+)\\|+(?<size>\\d+)\\|+(?<hash>[0-9A-z]{32})" +
                    "(\\|+[hH]=(?<h>[0-9A-z]{32})" +
                    "|\\|+[pP]=(?<p>[0-9A-z]{32}(:[0-9A-z]{32})*)" +
                    "|\\|+[sS]=(?<s>[^|]+))*" +
                    "(\\|/\\|sources,(?<sources>\\d{1,3}(\\.\\d{1,3}){3}:\\d{1,5})\\|/)?" +
                    "([|/]?|\\|/.*)"
    );
    private final String filename;
    private final long size;
    private final String hash;
    /**
     * may null
     */
    private final String rootHash;
    private final String partHash;
    private final String href;
    private final String sources;

    private Ed2kResource(String title, String filename, long size, String hash, String rootHash, String partHash,
                         String href, String sources) {
        super(title);
        this.filename = filename;
        this.size = size;
        this.hash = hash;
        this.rootHash = rootHash;
        this.partHash = partHash;
        this.href = href;
        this.sources = sources;
    }

    public static Ed2kResource of(String title, @Nonnull String url) {
        url = decode(url);
        Matcher matcher = URI_REGEX.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Not a valid %s url.", SCHEME));
        }
        return new Ed2kResource(
                title,
                matcher.group("name"),
                Long.parseLong(matcher.group("size")),
                matcher.group("hash"),
                matcher.group("h"),
                matcher.group("p"),
                matcher.group("s"),
                matcher.group("sources")
        );
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
        if (partHash != null) {
            builder.append("|p=").append(partHash);
        }
        if (href != null) {
            builder.append("|s=").append(href);
        }
        builder.append("|/");
        if (sources != null) {
            builder.append("|sources,").append(sources).append("|/");
        }
        return builder.toString();
    }
}
