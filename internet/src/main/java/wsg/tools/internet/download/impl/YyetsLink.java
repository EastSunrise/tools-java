package wsg.tools.internet.download.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.internet.download.InvalidResourceException;
import wsg.tools.internet.download.UnknownResourceException;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.base.FilenameSupplier;
import wsg.tools.internet.download.base.LengthSupplier;

/**
 * A yyets link.
 *
 * @author Kingen
 * @since 2020/12/6
 */
public final class YyetsLink extends AbstractLink implements FilenameSupplier, LengthSupplier {

    public static final String YYETS_PREFIX = "yyets://";

    private static final Pattern YYETS_REGEX = Pattern.compile(
        "yyets://N=(?<name>[^|]+)\\|S=(?<size>\\d+)\\|H=(?<hash>[0-9A-Za-z]{40})\\|",
        Pattern.CASE_INSENSITIVE);

    private final String name;
    private final long size;
    private final String hash;

    private YyetsLink(String title, String name, long size, String hash) {
        super(title);
        this.name = name;
        this.size = size;
        this.hash = hash;
    }

    public static YyetsLink of(String title, String url) throws InvalidResourceException {
        if (!StringUtils.startsWithIgnoreCase(url, YYETS_PREFIX)) {
            throw new UnknownResourceException(YyetsLink.class, title, url);
        }
        Matcher matcher = YYETS_REGEX.matcher(url);
        if (!matcher.matches()) {
            throw new InvalidResourceException(YyetsLink.class, title, url);
        }
        return new YyetsLink(title, matcher.group("name"), Long.parseLong(matcher.group("size")),
            matcher.group("hash"));
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
