package wsg.tools.internet.common;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.HttpSession;

import java.util.regex.Pattern;

/**
 * Utility for operations on sites.
 *
 * @author Kingen
 * @since 2021/3/1
 */
public final class SiteUtils {

    private static final char SEPARATOR = '.';
    private static final String[] COMMON_TOP_DOMAINS = new String[]{"com", "org", "net", "gov", "edu", "co", "top", "info"};
    private static final String[] REGION_TOP_DOMAINS = new String[]{"cn", "jp", "hk", "fr", "kr", "ru", "us", "uk"};
    private static final Pattern DOMAIN_REGEX = Pattern.compile("[a-z0-9][a-z0-9-]*[a-z0-9](\\.[a-z0-9][a-z0-9-]*[a-z0-9])+");

    /**
     * Splits the given domain.
     *
     * @param domain a full domain like 'xxx.xxx.xxx'
     * @return pair of main domain and sub domain if exists
     */
    public static Pair<String, String> splitDomain(String domain) {
        domain = AssertUtils.requireNotBlank(domain).toLowerCase();
        RegexUtils.matchesOrElseThrow(DOMAIN_REGEX, domain);
        String[] parts = StringUtils.split(domain, SEPARATOR);
        int last = parts.length - 1;
        int main = last - 1;
        String top = parts[last];
        if (ArrayUtils.contains(REGION_TOP_DOMAINS, top)) {
            if (ArrayUtils.contains(COMMON_TOP_DOMAINS, parts[last - 1])) {
                main--;
                top = parts[last - 1] + SEPARATOR + top;
            }
        }
        if (main < 0) {
            throw new IllegalArgumentException("Not a valid domain: " + domain);
        }
        if (main == 0) {
            return Pair.of(parts[main] + SEPARATOR + top, null);
        }
        return Pair.of(parts[main] + SEPARATOR + top, StringUtils.join(parts, SEPARATOR, 0, main));
    }

    /**
     * Validate the status of the site based on the annotation {@link SiteStatus}.
     *
     * @throws SiteStatusException if the status is abnormal
     */
    public static void validateStatus(Class<? extends HttpSession> clazz) throws SiteStatusException {
        SiteStatus annotation = clazz.getAnnotation(SiteStatus.class);
        if (annotation != null) {
            SiteStatus.Status status = annotation.status();
            if (!SiteStatus.Status.NORMAL.equals(status)) {
                throw new SiteStatusException(annotation);
            }
        }
    }
}
