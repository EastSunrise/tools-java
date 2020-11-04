package wsg.tools.internet.resource.entity.resource.valid;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.text.StringEscapeUtils;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;

import javax.annotation.Nonnull;
import java.util.LinkedList;
import java.util.List;
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
    private static final String XT = "urn:(btih|tree:tiger|sha1|ed2k|aich|kzhash|md5):([0-9A-z]{32}|[0-9A-z]{40})";
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

    private final List<String> topics;
    /**
     * may empty
     */
    private final List<String> names;
    private final List<String> trackers;
    private final List<Long> sizes;

    private MagnetResource(String title, List<String> topics, List<String> names, List<String> trackers, List<Long> sizes) {
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
        List<String> topics = new LinkedList<>();
        while (matcher.find()) {
            topics.add(matcher.group("xt"));
        }
        matcher = DN_REGEX.matcher(url);
        List<String> names = new LinkedList<>();
        while (matcher.find()) {
            String dn = matcher.group("dn");
            if (dn != null) {
                names.add(dn);
            }
        }
        matcher = TR_REGEX.matcher(url);
        List<String> trackers = new LinkedList<>();
        while (matcher.find()) {
            String tr = matcher.group("tr");
            if (tr != null) {
                trackers.add(tr);
            }
        }
        matcher = XL_REGEX.matcher(url);
        List<Long> sizes = new LinkedList<>();
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
