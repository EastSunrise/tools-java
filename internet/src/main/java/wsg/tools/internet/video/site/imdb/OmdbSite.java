package wsg.tools.internet.video.site.imdb;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpEntity;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.handler.CommaSeparatedNumberDeserializationProblemHandler;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Locale;

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

    private final String apikey;

    public OmdbSite(String apikey) {
        super("OMDb", "omdbapi.com");
        this.apikey = apikey;
    }

    /**
     * @see <a href="https://www.omdbapi.com/#parameters">By ID</a>
     */
    @Override
    public OmdbTitle getItemById(@Nonnull String imdbId) throws NotFoundException {
        OmdbTitle title = getObject(builder0("/").addParameter("i", imdbId).addParameter("plot", "full"), OmdbTitle.class);
        title.setImdbId(imdbId);
        return title;
    }

    /**
     * Get a season of TV series.
     *
     * @param season start with 1
     */
    public OmdbSeason season(String seriesId, int season) throws NotFoundException {
        return getObject(builder0("/")
                .addParameter("i", seriesId)
                .addParameter("season", String.valueOf(season)), OmdbSeason.class);
    }

    /**
     * Get an episode of TV series.
     *
     * @param season start with 1
     */
    public OmdbEpisode episode(String seriesId, int season, int episode) throws NotFoundException {
        return getObject(builder0("/")
                .addParameter("i", seriesId)
                .addParameter("season", String.valueOf(season))
                .addParameter("episode", String.valueOf(episode)), OmdbEpisode.class);
    }

    /**
     * Search subject fast.
     */
    public OmdbTitle search(String s) {
        return search(s, null, null, 1);
    }

    /**
     * Search subject.
     *
     * @param type {@link SearchTypeEnum}
     * @param page 1-100, default 1
     * @see <a href="https://www.omdbapi.com/#parameters">By Search</a>
     */
    public OmdbTitle search(String s, SearchTypeEnum type, Integer year, int page) {
        URIBuilder builder = builder0("/").addParameter("s", s);
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
        try {
            return getObject(builder, OmdbTitle.class);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    @Override
    public void handleRequest(RequestBuilder builder, HttpContext context) {
        builder.addToken("apikey", apikey);
    }

    @Override
    protected String handleEntity(HttpEntity entity) throws IOException {
        String content = super.handleEntity(entity);
        JsonNode node;
        try {
            node = mapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
        boolean response = Boolean.parseBoolean(node.get("Response").asText());
        if (response) {
            return content.replace("\"N/A\"", "null");
        } else {
            String error = node.get("Error").asText();
            if (NOT_FOUND_MSG.equals(error) ||
                    SEASON_NOT_FOUND_MSG.equals(error) || EPISODE_NOT_FOUND_MSG.equalsIgnoreCase(error)) {
                throw new NotFoundException(error);
            }
            throw new RuntimeException(error);
        }
    }

    @Override
    protected ObjectMapper objectMapper() {
        return super.objectMapper()
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                .setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
                .setLocale(Locale.ENGLISH)
                .registerModule(new SimpleModule()
                        .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                        .addDeserializer(RegionEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RegionEnum.class))
                        .addDeserializer(RatingEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RatingEnum.class))
                        .addDeserializer(GenreEnum.class, EnumDeserializers.getTextDeserializer(GenreEnum.class))
                )
                .registerModule(new JavaTimeModule())
                .addHandler(CommaSeparatedNumberDeserializationProblemHandler.INSTANCE);
    }

}
