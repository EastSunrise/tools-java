package wsg.tools.internet.video.site;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.jackson.deserializer.MoneyDeserializer;
import wsg.tools.common.jackson.deserializer.NumberDeserializersExt;
import wsg.tools.common.lang.Money;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.entity.imdb.ImdbSubject;
import wsg.tools.internet.video.enums.*;
import wsg.tools.internet.video.jackson.handler.SeparatedValueDeserializationProblemHandler;
import wsg.tools.internet.video.jackson.handler.YearDeserializationProblemHandler;

import java.io.IOException;
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
public final class OmdbSite extends BaseVideoSite {

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
    public ImdbSubject getSubjectById(String id) throws HttpResponseException {
        URI uri;
        try {
            uri = buildUri("/", null).addParameter("i", id).addParameter("plot", "full").build();
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        ResponseResult subject = getObject(uri, ResponseResult.class);
        if (!subject.response) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, subject.error);
        }
        return subject;
    }

    /**
     * Search subject fast.
     */
    public Subject searchSubject(String s) throws IOException, URISyntaxException {
        return searchSubject(s, null, null, 1);
    }

    /**
     * Search subject.
     *
     * @param type {@link SearchTypeEnum}
     * @param page 1-100, default 1
     * @see <a href="https://www.omdbapi.com/#parameters">By Search</a>
     */
    public Subject searchSubject(String s, SearchTypeEnum type, Integer year, int page) throws IOException, URISyntaxException {
        URIBuilder builder = buildUri("/", null).addParameter("s", s);
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
        return getObject(builder.build(), Subject.class);
    }

    @Override
    public ObjectMapper getObjectMapper() {
        ObjectMapper objectMapper = super.getObjectMapper();
        objectMapper.configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true);
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(Long.class, NumberDeserializersExt.LongDeserializer.INSTANCE)
                .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                .addDeserializer(CountryEnum.class, EnumDeserializers.getAkaDeserializer(String.class, CountryEnum.class))
                .addDeserializer(ImdbTypeEnum.class, EnumDeserializers.getAkaDeserializer(String.class, ImdbTypeEnum.class))
                .addDeserializer(RatedEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RatedEnum.class))
                .addDeserializer(GenreEnum.class, EnumDeserializers.getTextDeserializer(GenreEnum.class))
                .addDeserializer(Money.class, MoneyDeserializer.INSTANCE)
        ).registerModule(new JavaTimeModule()
                .addDeserializer(LocalDate.class,
                        new LocalDateDeserializer(DateTimeFormatter.ofPattern("dd MMM yyyy").withLocale(Locale.ENGLISH)))
        );
        objectMapper.addHandler(SeparatedValueDeserializationProblemHandler.getInstance(Constants.COMMA_DELIMITER))
                .addHandler(YearDeserializationProblemHandler.INSTANCE);
        return objectMapper;
    }

    @Override
    protected String getContent(URI uri) throws HttpResponseException {
        return super.getContent(uri).replace("\"N/A\"", "null");
    }

    @Override
    protected URIBuilder buildUri(String path, String lowDomain, Object... pathArgs) {
        return super.buildUri(path, lowDomain, pathArgs)
                .addParameter("apikey", apiKey);
    }

    @Setter
    @Getter
    static class ResponseResult extends ImdbSubject {
        Boolean response;
        String error;
    }
}
