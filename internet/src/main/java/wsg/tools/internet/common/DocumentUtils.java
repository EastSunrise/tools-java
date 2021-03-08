package wsg.tools.internet.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

/**
 * Utility for operations on a {@link org.jsoup.nodes.Document}.
 *
 * @author Kingen
 * @since 2021/3/6
 */
public final class DocumentUtils {

    private static final Pattern MINUTES_INTERVAL_REGEX = Pattern.compile("(?<m>\\d+)分钟前");
    private static final Pattern HOURS_INTERVAL_REGEX = Pattern.compile("(?<h>\\d+)小时前");
    private static final Pattern DAYS_INTERVAL_REGEX = Pattern.compile("(?<d>\\d+)天前");
    private static final Pattern WEEKS_INTERVAL_REGEX = Pattern.compile("(?<w>\\d+)周前");
    private static final Pattern MONTHS_INTERVAL_REGEX = Pattern.compile("(?<M>\\d+)个月前");
    private static final Pattern YEARS_INTERVAL_REGEX = Pattern.compile("(?<y>\\d+)年前");

    private DocumentUtils() {
    }

    /**
     * Parses a text of the interval between the target date and {@link LocalDateTime#now()}.
     *
     * @param interval the text of the interval
     * @return a {@code LocalDate} based on {@link LocalDateTime#now()} with the interval
     * subtracted, not null
     */
    public static LocalDateTime parseInterval(@Nonnull String interval) {
        Matcher minutes = MINUTES_INTERVAL_REGEX.matcher(interval);
        if (minutes.matches()) {
            return LocalDateTime.now().minusMinutes(Integer.parseInt(minutes.group("m")));
        }
        Matcher hours = HOURS_INTERVAL_REGEX.matcher(interval);
        if (hours.matches()) {
            return LocalDateTime.now().minusHours(Integer.parseInt(hours.group("h")));
        }
        Matcher days = DAYS_INTERVAL_REGEX.matcher(interval);
        if (days.matches()) {
            return LocalDateTime.now().minusDays(Integer.parseInt(days.group("d")));
        }
        Matcher weeks = WEEKS_INTERVAL_REGEX.matcher(interval);
        if (weeks.matches()) {
            return LocalDateTime.now().minusWeeks(Integer.parseInt(weeks.group("w")));
        }
        Matcher months = MONTHS_INTERVAL_REGEX.matcher(interval);
        if (months.matches()) {
            return LocalDateTime.now().minusMonths(Integer.parseInt(months.group("M")));
        }
        Matcher years = YEARS_INTERVAL_REGEX.matcher(interval);
        if (years.matches()) {
            return LocalDateTime.now().minusYears(Integer.parseInt(years.group("y")));
        }
        throw new IllegalArgumentException("Can't recognize the interval: " + interval);
    }

    /**
     * Collects all not-blank texts recursively.
     *
     * @param node the root node
     * @return list of not-blank texts
     */
    public static List<String> collectTexts(Node node) {
        List<String> texts = new ArrayList<>();
        collectTexts(node, texts);
        return texts;
    }

    private static void collectTexts(Node node, List<String> texts) {
        if (node instanceof TextNode) {
            String text = ((TextNode) node).text();
            if (StringUtils.isBlank(text)) {
                return;
            }
            texts.add(text.strip());
            return;
        }
        if (node instanceof Element) {
            for (Node childNode : node.childNodes()) {
                collectTexts(childNode, texts);
            }
            return;
        }
        throw new IllegalArgumentException("Unexpected type of node: " + node.getClass());
    }
}
