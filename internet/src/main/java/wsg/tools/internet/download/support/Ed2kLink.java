package wsg.tools.internet.download.support;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.download.view.FilenameSupplier;
import wsg.tools.internet.download.view.LengthSupplier;

/**
 * An ed2k link.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public final class Ed2kLink extends AbstractLink implements FilenameSupplier, LengthSupplier {

    public static final String ED2K_PREFIX = "ed2k://";
    private static final Pattern ED2K_REGEX = Pattern.compile(
        "ed2k://\\|file\\|(?<name>[^|]+)\\|(?<size>\\d+)\\|(?<hash>[0-9A-Za-z]{32})"
            + "(\\|h=(?<h>[0-9A-Za-z]{32})"
            + "|\\|p=(?<p>[0-9A-Za-z]{32}(:[0-9A-Za-z]{32})*)" + "|\\|s=(?<s>[^|]+))*"
            + "(\\|/\\|sources,(?<sources>\\d{1,3}(\\.\\d{1,3}){3}:\\d{1,5})\\|/"
            + "|(\\|/.*)|(\\|/\\|/)|(\\|)|)",
        Pattern.CASE_INSENSITIVE);
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

    private Ed2kLink(String title, String filename, long size, String hash, String rootHash,
        String partHash,
        String href, String sources) {
        super(title);
        this.filename = Objects.requireNonNull(filename);
        this.size = size;
        this.hash = Objects.requireNonNull(hash);
        this.rootHash = rootHash;
        this.partHash = partHash;
        this.href = href;
        this.sources = sources;
    }

    public static Ed2kLink of(String title, @Nonnull String url) throws InvalidResourceException {
        if (!StringUtils.startsWithIgnoreCase(url, ED2K_PREFIX)) {
            throw new UnknownResourceException(Ed2kLink.class, title, url);
        }
        url = url.replace(" ", "");
        Matcher matcher = ED2K_REGEX.matcher(url);
        if (matcher.lookingAt()) {
            return new Ed2kLink(title, matcher.group("name"), Long.parseLong(matcher.group("size")),
                matcher.group("hash"), matcher.group("h"), matcher.group("p"), matcher.group("s"),
                matcher.group("sources"));
        }
        throw new InvalidResourceException(Ed2kLink.class, title, url);
    }

    @Nonnull
    @Override
    public String getUrl() {
        StringBuilder builder = new StringBuilder("ed2k://|file");
        builder.append("|").append(filename).append("|").append(size).append("|").append(hash);
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

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public Long length() {
        return size;
    }
}
