package wsg.tools.internet.download.support;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import wsg.tools.common.lang.AssertUtils;

/**
 * A magnet link.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public final class MagnetLink extends AbstractLink {

    public static final String MAGNET_PREFIX = "magnet:?";

    private static final String XT =
        "urn:(btih|tree:tiger|sha1|ed2k|aich|kzhash|md5):([0-9A-Za-z]{40}|[0-9A-Za-z]{32})";
    private static final String XL = "\\d+";
    private static final String EQ = "(\\d+|\\.\\d+|\\+\\d+)?=";
    private static final String VAR = "[^&]*(&(?!(xl|tr|dn|xt)" + EQ + ")[^&]*)*";
    private static final Pattern XT_REGEX = Pattern
        .compile("(\\?1?xt1?|&xt)" + EQ + "(?<xt>" + XT + ")");
    private static final Pattern DN_REGEX = Pattern.compile("[?&]dn" + EQ + "(?<dn>" + VAR + ")");
    private static final Pattern TR_REGEX = Pattern.compile("[?&]tr" + EQ + "(?<tr>" + VAR + ")");
    private static final Pattern XL_REGEX = Pattern.compile("[?&]xl" + EQ + "(?<xl>" + XL + ")");
    private static final Pattern MAGNET_REGEX =
        Pattern.compile(
            "magnet:\\?((tr|dn)" + EQ + VAR + "&|xl" + EQ + XL + "&)*" + "+1?xt1?" + EQ + XT
                + "\\W?(&(tr|dn)" + EQ + VAR + "|&xl" + EQ + XL + ")*" + "(&t|&dn|&tr)?",
            Pattern.CASE_INSENSITIVE);
    private final Set<String> topics;
    /**
     * may empty
     */
    private final Set<String> names;
    private final Set<String> trackers;
    private final Set<Long> sizes;

    private MagnetLink(String title, Set<String> topics, Set<String> names, Set<String> trackers,
        Set<Long> sizes) {
        super(title);
        this.topics = AssertUtils.require(topics, CollectionUtils::isNotEmpty,
            "Magnet resource must contain at least one exact topic.");
        this.names = names;
        this.trackers = trackers;
        this.sizes = sizes;
    }

    public static MagnetLink of(String title, @Nonnull String url) throws InvalidResourceException {
        if (!StringUtils.startsWithIgnoreCase(url, MAGNET_PREFIX)) {
            throw new UnknownResourceException(MagnetLink.class, title, url);
        }
        url = url.replace(" ", "");
        url = StringEscapeUtils.unescapeHtml4(url);
        if (!MAGNET_REGEX.matcher(url).matches()) {
            throw new InvalidResourceException(MagnetLink.class, title, url);
        }
        Matcher xt = XT_REGEX.matcher(url);
        Set<String> topics = new HashSet<>();
        while (xt.find()) {
            topics.add(xt.group("xt"));
        }
        Matcher dns = DN_REGEX.matcher(url);
        Set<String> names = new HashSet<>();
        while (dns.find()) {
            String dn = dns.group("dn");
            if (dn != null) {
                names.add(dn);
            }
        }
        Matcher trs = TR_REGEX.matcher(url);
        Set<String> trackers = new HashSet<>();
        while (trs.find()) {
            String tr = trs.group("tr");
            if (tr != null) {
                trackers.add(tr);
            }
        }
        Matcher xl = XL_REGEX.matcher(url);
        Set<Long> sizes = new HashSet<>();
        while (xl.find()) {
            sizes.add(Long.valueOf(xl.group("xl")));
        }
        return new MagnetLink(title, topics, names, trackers, sizes);
    }

    @Nonnull
    @Override
    public String getUrl() {
        StringBuilder builder = new StringBuilder(MAGNET_PREFIX);
        builder.append(topics.stream().map(s -> "xt=" + s).collect(Collectors.joining("&")));
        for (String name : names) {
            builder.append("&dn=").append(name);
        }
        for (String tracker : trackers) {
            builder.append("&tr=").append(tracker);
        }
        for (Long size : sizes) {
            builder.append("&xl=").append(size);
        }
        return builder.toString();
    }
}
