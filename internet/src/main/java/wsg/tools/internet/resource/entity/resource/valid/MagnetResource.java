package wsg.tools.internet.resource.entity.resource.valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Resources of magnet.
 *
 * @author Kingen
 * @since 2020/9/18
 */
public class MagnetResource extends BaseValidResource {

    public static final String SCHEME = "magnet";
    private static final String XT = "urn:(btih|tree:tiger|sha1|ed2k|aich|kzhash|md5):([0-9A-z]{40}|[0-9A-z]{32})";
    private static final String VAR = "[^&]*(&(?!xl|tr|dn|xt)[^&]*)*";
    private static final String XL = "\\d+";
    private static final Pattern XT_REGEX = Pattern.compile("(\\?1?|&)xt(\\.\\d+)?=(?<xt>" + XT + ")");
    private static final Pattern DN_REGEX = Pattern.compile("[?&]dn(\\.\\d+)?=(?<dn>" + VAR + ")");
    private static final Pattern TR_REGEX = Pattern.compile("[?&]tr(\\.\\d+)?=(?<tr>" + VAR + ")");
    private static final Pattern XL_REGEX = Pattern.compile("[?&]xl(\\.\\d+)?=(?<xl>\\d+)");
    private static final Pattern URL_REGEX = Pattern.compile(
            "magnet:\\?(xt(\\.\\d+)?=" + XT + "&+|(tr|dn)(\\.\\d+)?(=" + VAR + ")?&+|xl(\\.\\d+)?=" + XL + "&+)*" +
                    "1?xt(\\.\\d+)?=" + XT +
                    "(&+xt(\\.\\d+)?=" + XT + "|&+(tr|dn)(\\.\\d+)?(=" + VAR + ")?|&+xl(\\.\\d+)?=" + XL + ")*" +
                    "(&+(?!xl|tr|dn|xt)[^&]*)?"
    );

    private final Set<String> topics;
    /**
     * may empty
     */
    private final Set<String> names;
    private final Set<String> trackers;
    private final Set<Long> sizes;

    private MagnetResource(String title, Set<String> topics, Set<String> names, Set<String> trackers, Set<Long> sizes) {
        super(title);
        AssertUtils.test(topics, CollectionUtils::isNotEmpty, "Magnet resource must contain at least one exact topic.");
        this.topics = topics;
        this.names = names;
        this.trackers = trackers;
        this.sizes = sizes;
    }

    public static MagnetResource of(String title, @Nonnull String url) {
        url = decode(url);
        url = StringEscapeUtils.unescapeHtml4(url);
        if (!URL_REGEX.matcher(url).matches()) {
            throw new IllegalArgumentException(String.format("Not a valid %s url.", SCHEME));
        }
        Matcher matcher = XT_REGEX.matcher(url);
        Set<String> topics = new HashSet<>();
        while (matcher.find()) {
            topics.add(matcher.group("xt"));
        }
        matcher = DN_REGEX.matcher(url);
        Set<String> names = new HashSet<>();
        while (matcher.find()) {
            String dn = matcher.group("dn");
            if (dn != null) {
                names.add(dn);
            }
        }
        matcher = TR_REGEX.matcher(url);
        Set<String> trackers = new HashSet<>();
        while (matcher.find()) {
            String tr = matcher.group("tr");
            if (tr != null) {
                trackers.add(tr);
            }
        }
        matcher = XL_REGEX.matcher(url);
        Set<Long> sizes = new HashSet<>();
        while (matcher.find()) {
            sizes.add(Long.valueOf(matcher.group("xl")));
        }
        return new MagnetResource(title, topics, names, trackers, sizes);
    }

    @Override
    public String getUrl() {
        StringBuilder builder = new StringBuilder("magnet:?");
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
