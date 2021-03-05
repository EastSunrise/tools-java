package wsg.tools.internet.movie.imdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Locale;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.impl.BasicHttpSession;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.enums.Region;
import wsg.tools.internet.movie.common.enums.ImdbRating;
import wsg.tools.internet.movie.common.enums.MovieGenre;
import wsg.tools.internet.movie.common.enums.RatingSource;
import wsg.tools.internet.movie.common.enums.SearchType;
import wsg.tools.internet.movie.common.jackson.CommaSeparatedNumberDeserializationProblemHandler;

/**
 * @author Kingen
 * @see <a href="https://www.omdbapi.com/">OMDb</a>
 * @since 2020/6/18
 */
public final class OmdbSite extends BaseSite implements ImdbRepository<OmdbTitle> {

    private static final int MAX_PAGE = 100;
    private static final String NOT_FOUND_MSG = "Error getting data.";
    private static final String SEASON_NOT_FOUND_MSG = "Series or season not found!";
    private static final String EPISODE_NOT_FOUND_MSG = "Series or episode not found!";
    private static final ObjectMapper MAPPER = new ObjectMapper()
        .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
        .setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
        .setLocale(Locale.ENGLISH)
        .registerModule(new SimpleModule()
            .addDeserializer(
                Language.class, EnumDeserializers.getAkaDeserializer(String.class, Language.class))
            .addDeserializer(
                Region.class, EnumDeserializers.getAkaDeserializer(String.class, Region.class))
            .addDeserializer(
                ImdbRating.class,
                EnumDeserializers.getAkaDeserializer(String.class, ImdbRating.class))
            .addDeserializer(MovieGenre.class,
                EnumDeserializers.getTextDeserializer(MovieGenre.class))
            .addDeserializer(RatingSource.class,
                EnumDeserializers.getTextDeserializer(RatingSource.class)))
        .registerModule(new JavaTimeModule())
        .addHandler(new CommaSeparatedNumberDeserializationProblemHandler());

    private final String apikey;

    public OmdbSite(String apikey) {
        super("OMDb", new BasicHttpSession("omdbapi.com"), new OmdbResponseHandler());
        this.apikey = Objects.requireNonNull(apikey);
    }

    /**
     * @see <a href="https://www.omdbapi.com/#parameters">By ID</a>
     */
    @Override
    public OmdbTitle findById(@Nonnull String imdbId) throws HttpResponseException {
        OmdbTitle title =
            getObject(builder0("/").addParameter("i", imdbId).addParameter("plot", "full"),
                OmdbTitle.class);
        title.setImdbId(imdbId);
        return title;
    }

    /**
     * Get a season of TV series.
     *
     * @param season start with 1
     */
    public OmdbSeason season(String seriesId, int season) throws HttpResponseException {
        return getObject(builder0("/").addParameter("i", seriesId)
                .addParameter("season", String.valueOf(season)),
            OmdbSeason.class);
    }

    /**
     * Get an episode of TV series.
     *
     * @param season start with 1
     */
    public OmdbEpisode episode(String seriesId, int season, int episode) throws HttpResponseException {
        return getObject(
            builder0("/").addParameter("i", seriesId).addParameter("season", String.valueOf(season))
                .addParameter("episode", String.valueOf(episode)), OmdbEpisode.class);
    }

    /**
     * Search subject fast.
     */
    public OmdbTitle search(String s) throws HttpResponseException {
        return search(s, null, null, 1);
    }

    /**
     * Search subject.
     *
     * @param type {@link SearchType}
     * @param page 1-100, default 1
     * @see <a href="https://www.omdbapi.com/#parameters">By Search</a>
     */
    public OmdbTitle search(String s, SearchType type, Integer year, int page)
        throws HttpResponseException {
        RequestBuilder builder = builder0("/").addParameter("s", s);
        if (type != null) {
            builder.addParameter("type", type.toString().toLowerCase());
        }
        if (year != null) {
            builder.addParameter("y", year.toString());
        }
        if (page < 1 || page > MAX_PAGE) {
            throw new IllegalArgumentException("Illegal page " + page + ", required: 1-100");
        } else {
            builder.addParameter("page", String.valueOf(page));
        }
        return getObject(builder, OmdbTitle.class);
    }

    private <T> T getObject(RequestBuilder builder, Class<T> clazz) throws HttpResponseException {
        builder.setToken("apikey", apikey);
        return getObject(builder, MAPPER, clazz, SnapshotStrategy.never());
    }

    private static class OmdbResponseHandler extends AbstractResponseHandler<String> {

        @Override
        public String handleEntity(HttpEntity entity) throws IOException {
            String content = EntityUtils.toString(entity);
            JsonNode node;
            try {
                node = MAPPER.readTree(content);
            } catch (JsonProcessingException e) {
                throw AssertUtils.runtimeException(e);
            }
            boolean response = Boolean.parseBoolean(node.get("Response").asText());
            if (response) {
                return content.replace("\"N/A\"", "null");
            } else {
                String error = node.get("Error").asText();
                if (NOT_FOUND_MSG.equals(error) || SEASON_NOT_FOUND_MSG.equals(error)
                    || EPISODE_NOT_FOUND_MSG.equalsIgnoreCase(error)) {
                    throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, error);
                }
                throw new RuntimeException(error);
            }
        }
    }
}
