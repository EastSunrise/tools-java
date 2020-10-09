package wsg.tools.internet.resource.entity.resource;

import lombok.Getter;
import org.apache.commons.lang3.ArrayUtils;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.util.AssertUtils;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of magnet.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class MagnetResource extends AbstractResource {

    public static final String SCHEME = "magnet";
    private static final int[] HASH_LENGTHS = new int[]{32, 40};
    private static final Pattern URI_REGEX = Pattern.compile(
            "magnet:(\\?|%3F)" +
                    "1?xt(=|%3D)(?<xt>urn:(tree:tiger|sha1|ed2k|aich|kzhash|btih|md5|crc32):(?<hash>[0-9A-z]+))" +
                    "((&|%26)tr(\\.\\d)?(=|%3D)[^&=]*|(&|%26|%26amp%3B|&amp;)dn(=|%3D)(?<dn>[^&=]+(&(?!xt|dn|tr|xl)|(?!xt|dn|tr|xl)=)?[^&=]+)|(&|%26)xl(=|%3D)(?<xl>\\d+))*" +
                    "(&|&tr)?");
    private static final Pattern TR_REGEX = Pattern.compile("tr(\\.\\d)?(=|%3D)(?<tr>[^&=]+)");
    private final String xt;
    /**
     * may null
     */
    private final String displayName;
    private final List<String> trackers;
    /**
     * -1 if not specified
     */
    private final long size;

    public MagnetResource(String url) {
        url = URLDecoder.decode(url, Constants.UTF_8);
        Matcher matcher = AssertUtils.matches(URI_REGEX, url);
        this.xt = matcher.group("xt");
        String hash = matcher.group("hash");
        if (!ArrayUtils.contains(HASH_LENGTHS, hash.length())) {
            throw new IllegalArgumentException("Wrong length of hash: " + hash.length() + ", required: 32/40.");
        }
        this.displayName = matcher.group("dn");
        List<String> trackers = new ArrayList<>();
        Matcher trMatcher = TR_REGEX.matcher(url);
        while (trMatcher.find()) {
            trackers.add(trMatcher.group("tr"));
        }
        this.trackers = trackers;
        String xl = matcher.group("xl");
        size = xl == null ? -1 : Long.parseLong(xl);
    }

    @Override
    public String getUrl() {
        StringBuilder builder = new StringBuilder("magnet:?");
        builder.append("xt=").append(xt);
        if (displayName != null) {
            builder.append("&dn=").append(displayName);
        }
        for (String tracker : trackers) {
            builder.append("&tr=").append(tracker);
        }
        if (size >= 0) {
            builder.append("&dl=").append(size);
        }
        return builder.toString();
    }

    @Override
    public String filename() {
        return displayName;
    }

    @Override
    public long size() {
        return size;
    }
}
