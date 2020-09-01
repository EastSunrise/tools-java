package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.exc.InvalidTypeIdException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import wsg.tools.common.constant.SignConstants;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.jackson.deserializer.MoneyDeserializer;
import wsg.tools.common.jackson.deserializer.NumberDeserializersExt;
import wsg.tools.common.lang.Money;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.ContentTypeEnum;
import wsg.tools.internet.video.entity.omdb.base.BaseOmdbTitle;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.handler.SeparatedValueDeserializationProblemHandler;
import wsg.tools.internet.video.jackson.handler.YearDeserializationProblemHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

/**
 * <a href="https://www.omdbapi.com/">OMDb</a>
 * <p>
 * An api key is required.
 *
 * @author Kingen
 * @since 2020/6/18
 */
public final class OmdbSite extends BaseSite {

    private static final int MAX_PAGE = 100;

    private final String apiKey;

    public OmdbSite(String apiKey) {
        super("OMDb", "omdbapi.com");
        this.apiKey = apiKey;
    }

    /**
     * Get subject by id exactly.
     *
     * @see <a href="https://www.omdbapi.com/#parameters">By ID</a>
     */
    public BaseOmdbTitle getSubjectById(String id) throws HttpResponseException {
        return getResponse(buildUri()
                .addParameter("i", id).addParameter("plot", "full"));
    }

    /**
     * Search subject fast.
     */
    public BaseOmdbTitle searchSubject(String s) throws HttpResponseException {
        return searchSubject(s, null, null, 1);
    }

    /**
     * Search subject.
     *
     * @param type {@link SearchTypeEnum}
     * @param page 1-100, default 1
     * @see <a href="https://www.omdbapi.com/#parameters">By Search</a>
     */
    public BaseOmdbTitle searchSubject(String s, SearchTypeEnum type, Integer year, int page) throws HttpResponseException {
        URIBuilder builder = buildUri().addParameter("s", s);
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
        return getResponse(builder);
    }

    private BaseOmdbTitle getResponse(URIBuilder builder) throws HttpResponseException {
        URI uri;
        try {
            uri = builder.build();
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        String json = getCachedContent(uri, ContentTypeEnum.JSON);
        BaseOmdbTitle title;
        try {
            title = objectMapper.readValue(json, BaseOmdbTitle.class);
        } catch (InvalidTypeIdException e) {
            throw new HttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
        if (!title.getResponse()) {
            throw new HttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, title.getError());
        }
        return title;
    }

    @Override
    protected void setObjectMapper() {
        super.setObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
                .setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE)
                .registerModule(new SimpleModule()
                        .addDeserializer(Long.class, NumberDeserializersExt.LongDeserializer.INSTANCE)
                        .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                        .addDeserializer(CountryEnum.class, EnumDeserializers.getAkaDeserializer(String.class, CountryEnum.class))
                        .addDeserializer(RatedEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RatedEnum.class))
                        .addDeserializer(GenreEnum.class, EnumDeserializers.getTextDeserializer(GenreEnum.class))
                        .addDeserializer(Money.class, MoneyDeserializer.INSTANCE)
                ).registerModule(new JavaTimeModule()
                .addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.ENGLISH))
                ))
                .addHandler(SeparatedValueDeserializationProblemHandler.getInstance(SignConstants.COMMA))
                .addHandler(YearDeserializationProblemHandler.INSTANCE);
    }

    @Override
    protected String getContent(URI uri) throws HttpResponseException {
        return super.getContent(uri).replace("\"N/A\"", "null");
    }

    protected URIBuilder buildUri(Object... pathArgs) {
        return super.buildPath("/", pathArgs)
                .addParameter("apikey", apiKey);
    }
}
