package wsg.tools.internet.movie.omdb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.RequestBuilder;
import org.jetbrains.annotations.Contract;
import wsg.tools.common.jackson.EnumDeserializers;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.ConcreteSite;
import wsg.tools.internet.base.page.Page;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.enums.Language;
import wsg.tools.internet.common.enums.Region;
import wsg.tools.internet.movie.common.enums.MovieGenre;
import wsg.tools.internet.movie.common.enums.RatingSource;

/**
 * @author Kingen
 * @since 2020/6/18
 */
@ConcreteSite
public final class OmdbSite extends BaseSite implements OmdbApi {

    private static final String NOT_FOUND_MSG = "not found!";

    private final String apikey;

    public OmdbSite(String apikey) {
        super("The Open Movie Database", httpsHost("www.omdbapi.com"));
        this.apikey = AssertUtils.requireNotBlank(apikey);
    }

    @Override
    public String getApikey() {
        return apikey;
    }

    @Nonnull
    @Contract("_, _, _ -> new")
    @Override
    public Page<AbstractOmdbMovie> searchMovies(String keyword, @Nonnull PageIndex pageIndex,
        @Nullable OmdbOptionalReq optionalReq) throws NotFoundException, OtherResponseException {
        AssertUtils.requireNotBlank(keyword);
        RequestBuilder builder = httpGet("/").addParameter("s", keyword);
        addOptionalReq(builder, optionalReq);
        builder.addParameter("r", "json");
        builder.addParameter("page", String.valueOf(pageIndex.getCurrent() + 1));
        OmdbSearchResult result = getObject(builder, Lazy.MAPPER, OmdbSearchResult.class);
        return Page.amountCountable(result.getItems(), pageIndex, 10, result.getTotal());
    }

    @Nonnull
    @Override
    public AbstractOmdbMovie findMovie(@Nonnull OmdbReq req, @Nullable OmdbOptionalReq optionalReq)
        throws NotFoundException, OtherResponseException {
        RequestBuilder builder = httpGet("/");
        addReq(builder, req);
        addOptionalReq(builder, optionalReq);
        builder.addParameter("plot", "full");
        builder.addParameter("r", "json");
        return getObject(builder, Lazy.MAPPER, AbstractOmdbMovie.class);
    }

    @Override
    public OmdbSeason findSeason(@Nonnull OmdbReq req, int season)
        throws NotFoundException, OtherResponseException {
        AssertUtils.requireRange(season, 1, null);
        RequestBuilder builder = httpGet("/");
        addReq(builder, req);
        builder.addParameter("season", String.valueOf(season));
        return getObject(builder, Lazy.MAPPER, OmdbSeason.class);
    }

    @Override
    public OmdbEpisode findEpisode(@Nonnull OmdbReq req, int season, int episode)
        throws NotFoundException, OtherResponseException {
        AssertUtils.requireRange(season, 1, null);
        AssertUtils.requireRange(episode, 1, null);
        RequestBuilder builder = httpGet("/");
        addReq(builder, req);
        builder.addParameter("season", String.valueOf(season));
        builder.addParameter("episode", String.valueOf(episode));
        return getObject(builder, Lazy.MAPPER, OmdbEpisode.class);
    }

    private void addReq(RequestBuilder builder, OmdbReq req) {
        if (req.getImdbId() != null) {
            builder.addParameter("i", req.getImdbId());
        }
        if (req.getTitle() != null) {
            builder.addParameter("t", req.getTitle());
        }
    }

    private void addOptionalReq(RequestBuilder builder, OmdbOptionalReq optionalReq) {
        if (optionalReq != null) {
            if (optionalReq.getType() != null) {
                builder.addParameter("type", optionalReq.getType().getAsPath());
            }
            if (optionalReq.getYear() != 0) {
                builder.addParameter("y", String.valueOf(optionalReq.getYear()));
            }
        }
    }

    @Override
    public String getContent(@Nonnull RequestBuilder builder) throws IOException {
        return super.getContent(builder).replace("\"N/A\"", "null");
    }

    @Override
    public <T> T getObject(@Nonnull RequestBuilder builder, @Nonnull ObjectMapper mapper,
        Class<? extends T> clazz) throws NotFoundException, OtherResponseException {
        builder.addParameter("apikey", apikey);
        T t = super.getObject(builder, mapper, clazz);
        if (t instanceof OmdbResponse) {
            OmdbResponse response = (OmdbResponse) t;
            if (!response.isSuccess()) {
                String error = response.getError();
                if (error.endsWith(NOT_FOUND_MSG)) {
                    throw new NotFoundException(error);
                }
                throw new OtherResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, error);
            }
        }
        return t;
    }

    private static class Lazy {

        private static final ObjectMapper MAPPER = new ObjectMapper()
            .setLocale(Locale.ENGLISH)
            .registerModule(new SimpleModule()
                .addDeserializer(RatingSource.class, EnumDeserializers.ofAlias(RatingSource.class))
                .addDeserializer(MovieGenre.class, EnumDeserializers.ofAlias(MovieGenre.class))
                .addDeserializer(Language.class, EnumDeserializers.ofAlias(Language.class))
                .addDeserializer(Region.class, EnumDeserializers.ofAlias(Region.class))
            ).registerModule(new JavaTimeModule())
            .addHandler(new OmdbDeserializationProblemHandler());
    }
}
