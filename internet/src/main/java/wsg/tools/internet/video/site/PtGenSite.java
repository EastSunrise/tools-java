package wsg.tools.internet.video.site;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.jackson.deserializer.MoneyDeserializer;
import wsg.tools.common.lang.Money;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.gen.base.BaseGenImdbTitle;
import wsg.tools.internet.video.entity.gen.base.BaseGenResponse;
import wsg.tools.internet.video.entity.gen.object.GenDoubanSubject;
import wsg.tools.internet.video.enums.CountryEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatedEnum;
import wsg.tools.internet.video.jackson.deserializer.YearDeserializerExt;
import wsg.tools.internet.video.jackson.handler.SingletonListDeserializationProblemHandler;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.Year;

/**
 * <a href="https://ptgen.rhilip.workers.dev/">PT Gen</a>
 *
 * @author Kingen
 * @since 2020/8/29
 */
public class PtGenSite extends BaseSite {

    public PtGenSite() {
        super("PT Gen", "rhilip.info");
    }

    @Override
    protected void setObjectMapper() {
        super.setObjectMapper();
        objectMapper
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerModule(new SimpleModule()
                        .addDeserializer(GenreEnum.class, EnumDeserializers.getAkaDeserializer(String.class, GenreEnum.class))
                        .addDeserializer(RatedEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RatedEnum.class))
                        .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                        .addDeserializer(CountryEnum.class, EnumDeserializers.getAkaDeserializer(String.class, CountryEnum.class))
                        .addDeserializer(Money.class, MoneyDeserializer.INSTANCE)
                )
                .registerModule(new JavaTimeModule()
                        .addDeserializer(Year.class, YearDeserializerExt.INSTANCE)
                )
                .addHandler(SingletonListDeserializationProblemHandler.INSTANCE);
    }

    public GenDoubanSubject doubanSubject(long dbId) throws HttpResponseException {
        return gen("douban", String.valueOf(dbId), GenDoubanSubject.class);
    }

    public GenDoubanSubject doubanSubject(String imdbId) throws HttpResponseException {
        return gen("douban", imdbId, GenDoubanSubject.class);
    }

    public BaseGenImdbTitle imdbSubject(String imdbId) throws HttpResponseException {
        return gen("imdb", imdbId, BaseGenImdbTitle.class);
    }

    /**
     * Gen info of movie
     *
     * @param site douban, imdb
     * @param sid  tt\d+ or db id
     */
    private <T extends BaseGenResponse> T gen(String site, String sid, Class<T> clazz) throws HttpResponseException {
        try {
            URI uri = buildPath("/tool/movieinfo/gen")
                    .addParameter("site", site)
                    .addParameter("sid", sid)
                    .build();
            T subject = getObject(uri, clazz);
            if (subject.isSuccess()) {
                return subject;
            }
            throw new HttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, subject.getError());
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    @Override
    protected URIBuilder buildPath(String path, Object... pathArgs) {
        return super.buildPath(path, pathArgs).setHost("api." + domain);
    }
}
