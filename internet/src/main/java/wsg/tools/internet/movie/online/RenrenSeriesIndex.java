package wsg.tools.internet.movie.online;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import wsg.tools.common.jackson.JsonJoinedValue;
import wsg.tools.common.lang.StringUtilsExt;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.view.IntIdentifier;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.common.TitleSupplier;
import wsg.tools.internet.movie.common.enums.MovieGenre;

/**
 * An index which points to a series on the site.
 *
 * @author Kingen
 * @since 2021/3/22
 */
public class RenrenSeriesIndex implements IntIdentifier, TitleSupplier, CoverSupplier {

    @JsonProperty("id")
    private int id;
    @JsonProperty("title")
    private String title;
    @JsonProperty("cover")
    private URL cover;
    @JsonProperty("type")
    @JsonJoinedValue(separator = "/")
    private List<MovieGenre> genres;
    @JsonProperty("score")
    private double score;
    @JsonProperty("seasonNum")
    @JsonDeserialize(using = SeasonDeserializer.class)
    private int season;

    RenrenSeriesIndex() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    public List<MovieGenre> getGenres() {
        return genres;
    }

    public double getScore() {
        return score;
    }

    public int getSeason() {
        return season;
    }

    private static class SeasonDeserializer extends StdDeserializer<Integer> {

        private static final Pattern SEASON_REGEX = Pattern.compile("第(?<s>.+)季");

        protected SeasonDeserializer() {
            super(Integer.class);
        }

        @Override
        public Integer deserialize(@Nonnull JsonParser p, DeserializationContext ctxt)
            throws IOException {
            if (p.hasToken(JsonToken.VALUE_NUMBER_INT)) {
                return p.getIntValue();
            }
            if (p.hasToken(JsonToken.VALUE_STRING)) {
                String text = p.getValueAsString();
                Matcher matcher = RegexUtils.matchesOrElseThrow(SEASON_REGEX, text);
                return StringUtilsExt.parseChinesNumeric(matcher.group("s"));
            }
            return (Integer) ctxt.handleUnexpectedToken(_valueClass, p);
        }
    }
}
