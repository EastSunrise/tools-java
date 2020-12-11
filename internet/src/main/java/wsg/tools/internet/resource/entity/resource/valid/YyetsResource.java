package wsg.tools.internet.resource.entity.resource.valid;

import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;
import wsg.tools.internet.resource.entity.resource.base.FilenameSupplier;
import wsg.tools.internet.resource.entity.resource.base.LengthSupplier;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of YYETS.
 *
 * @author Kingen
 * @since 2020/12/6
 */
public class YyetsResource extends BaseValidResource implements FilenameSupplier, LengthSupplier {

    public static final String SCHEME = "yyets";

    private static final Pattern URI_REGEX =
            Pattern.compile("yyets://N=(?<name>[^|]+)\\|S=(?<size>\\d+)\\|H=(?<hash>[0-9A-z]{40})\\|");

    private final String name;
    private final long size;
    private final String hash;

    private YyetsResource(String title, String name, long size, String hash) {
        super(title);
        this.name = name;
        this.size = size;
        this.hash = hash;
    }

    public static YyetsResource of(String title, String url) {
        Matcher matcher = URI_REGEX.matcher(url);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format("Not a valid %s url.", SCHEME));
        }
        return new YyetsResource(title, matcher.group("name"),
                Long.parseLong(matcher.group("size")), matcher.group("hash"));
    }

    @Override
    public String getUrl() {
        return String.format("yyets://N=%s|S=%d|H=%s", name, size, hash);
    }

    @Override
    public String getFilename() {
        return name;
    }

    @Override
    public Long length() {
        return size;
    }
}
