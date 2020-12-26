package wsg.tools.internet.resource.entity.rrys.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import wsg.tools.common.jackson.deserializer.AbstractStringDeserializer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize a string to {@link PlayStatus}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
public class PlayStatusDeserializer extends AbstractStringDeserializer<PlayStatus> {

    public static final PlayStatusDeserializer INSTANCE = new PlayStatusDeserializer();

    private static final String MOVIE_UNRELEASED = "未上映";
    private static final String MOVIE_RELEASED = "已上映";
    private static final String SERIES_NOT_START = "尚未开播";
    private static final String SERIES_STARTED = "已播出";
    private static final Pattern SERIES_SERIAL_REGEX = Pattern.compile("(单剧|mini剧|前传|周边资源)连载中");
    private static final Pattern SERIES_FINISHED_REGEX = Pattern.compile("(单剧|mini剧|本剧)完结");
    private static final Pattern SERIES_SEASON_NOT_START_REGEX = Pattern.compile("准备开播第(?<s>\\d+)季");
    private static final Pattern SERIES_SEASON_SERIAL_REGEX = Pattern.compile("第(?<s>\\d+)季连载中");
    private static final Pattern SERIES_SEASON_FINISHED_REGEX = Pattern.compile("第(?<s>\\d+)季完结");

    protected PlayStatusDeserializer() {
        super(PlayStatus.class);
    }

    @Override
    protected PlayStatus parseText(String text, DeserializationContext context) {
        if (MOVIE_UNRELEASED.equals(text)) {
            return MoviePlayStatus.UNRELEASED;
        }
        if (MOVIE_RELEASED.equals(text)) {
            return MoviePlayStatus.RELEASED;
        }

        if (SERIES_NOT_START.equals(text)) {
            return SeriesPlayStatus.SERIES_NOT_START;
        }
        if (SERIES_STARTED.equals(text)) {
            return SeriesPlayStatus.SERIES_SERIALIZED;
        }
        Matcher matcher = SERIES_SERIAL_REGEX.matcher(text);
        if (matcher.matches()) {
            return SeriesPlayStatus.SERIES_SERIALIZED;
        }
        matcher = SERIES_FINISHED_REGEX.matcher(text);
        if (matcher.matches()) {
            return SeriesPlayStatus.SERIES_FINISHED;
        }

        matcher = SERIES_SEASON_NOT_START_REGEX.matcher(text);
        if (matcher.matches()) {
            return new SeriesPlayStatus(Integer.parseInt(matcher.group("s")), SeriesPlayStatus.SeasonStatus.NOT_START);
        }
        matcher = SERIES_SEASON_SERIAL_REGEX.matcher(text);
        if (matcher.matches()) {
            return new SeriesPlayStatus(Integer.parseInt(matcher.group("s")), SeriesPlayStatus.SeasonStatus.SERIALIZED);
        }
        matcher = SERIES_SEASON_FINISHED_REGEX.matcher(text);
        if (matcher.matches()) {
            return new SeriesPlayStatus(Integer.parseInt(matcher.group("s")), SeriesPlayStatus.SeasonStatus.FINISHED);
        }
        throw new IllegalArgumentException("Can't parse PlayStatus from '" + text + "'");
    }
}
