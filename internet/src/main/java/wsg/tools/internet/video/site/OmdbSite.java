package wsg.tools.internet.video.site;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import lombok.Setter;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import wsg.tools.common.jackson.deserializer.impl.EnumAkaStringDeserializer;
import wsg.tools.common.jackson.deserializer.impl.LocalDateDeserializer;
import wsg.tools.common.jackson.deserializer.impl.MoneyDeserializer;
import wsg.tools.common.jackson.deserializer.impl.YearDeserializer;
import wsg.tools.common.lang.Money;
import wsg.tools.internet.video.entity.Subject;
import wsg.tools.internet.video.enums.RatedEnum;
import wsg.tools.internet.video.enums.SearchTypeEnum;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.Year;
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
public class OmdbSite extends AbstractVideoSite {

    private static final int MAX_PAGE = 100;

    private String apiKey;

    public OmdbSite(String apiKey) {
        super("OMDb", "omdbapi.com");
        this.apiKey = apiKey;
    }

    /**
     * Get subject by id exactly.
     *
     * @see <a href="https://www.omdbapi.com/#parameters">By ID</a>
     */
    public Subject getSubjectById(String id) throws IOException, URISyntaxException {
        URI uri = buildUri("/", null, Parameter.of("i", id), Parameter.of("plot", "full")).build();
        ResponseResult subject = getObject(uri, ResponseResult.class);
        if (!subject.response) {
            throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, subject.error);
        }
        subject.setText(subject.getTitle());
        subject.setTitle(null);
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
        URIBuilder builder = buildUri("/", null, Parameter.of("s", s));
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
                .addDeserializer(LocalDate.class,
                        new LocalDateDeserializer(DateTimeFormatter.ofPattern("d MMM yyy").withLocale(Locale.ENGLISH)))
                .addDeserializer(RatedEnum.class, EnumAkaStringDeserializer.of(RatedEnum.class))
                .addDeserializer(Year.class, new YearDeserializer(DateTimeFormatter.ofPattern("yyyy–")))
                .addDeserializer(Money.class, new MoneyDeserializer())
        );
        return objectMapper;
    }

    @Override
    protected String getContent(URI uri) throws IOException {
        return super.getContent(uri).replace("\"N/A\"", "null");
    }

    @Override
    protected URIBuilder buildUri(String path, String lowDomain, Parameter... params) {
        URIBuilder builder = super.buildUri(path, lowDomain, params);
        builder.addParameter("apikey", apiKey);
        return builder;
    }

    @Setter
    static class ResponseResult extends Subject {
        boolean response;
        String error;
    }
}
