package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.gen.base.BaseGenImdbTitle;
import wsg.tools.internet.video.entity.gen.base.BaseGenResponse;
import wsg.tools.internet.video.entity.gen.object.GenDoubanSubject;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RatingEnum;
import wsg.tools.internet.video.enums.RegionEnum;

/**
 * <a href="https://ptgen.rhilip.workers.dev/">PT Gen</a>
 *
 * @author Kingen
 * @since 2020/8/29
 */
@Deprecated
public class PtGenSite extends BaseSite {

    private static final String NOT_FOUND_MSG = "The corresponding resource does not exist.";

    public PtGenSite() {
        super("PT Gen", "rhilip.info");
    }

    @Override
    protected ObjectMapper objectMapper() {
        return super.objectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .registerModule(new SimpleModule()
                        .addDeserializer(GenreEnum.class, EnumDeserializers.getAkaDeserializer(String.class, GenreEnum.class))
                        .addDeserializer(RatingEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RatingEnum.class))
                        .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                        .addDeserializer(RegionEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RegionEnum.class))
                )
                .registerModule(new JavaTimeModule());
    }

    public GenDoubanSubject doubanSubject(long dbId) throws HttpResponseException {
        return gen("douban", String.valueOf(dbId), GenDoubanSubject.class);
    }

    public GenDoubanSubject doubanSubject(String imdbId) throws HttpResponseException {
        return gen("douban", imdbId, GenDoubanSubject.class);
    }

    public BaseGenImdbTitle imdbTitle(String imdbId) throws HttpResponseException {
        return gen("imdb", imdbId, BaseGenImdbTitle.class);
    }

    /**
     * Gen info of movie
     *
     * @param site douban, imdb
     * @param sid  tt\d+ or db id
     */
    private <T extends BaseGenResponse> T gen(String site, String sid, Class<T> clazz) throws HttpResponseException {
        URIBuilder builder = uriBuilder("/tool/movieinfo/gen")
                .addParameter("site", site)
                .addParameter("sid", sid);
        return getObject(builder, clazz);
    }

    @Override
    protected String handleJson(String json) throws JsonProcessingException, HttpResponseException {
        JsonNode node = objectMapper.readTree(json);
        boolean response = node.get("success").asBoolean();
        if (response) {
            return json;
        } else {
            String error = node.get("error").asText();
            if (NOT_FOUND_MSG.equals(error)) {
                throw new HttpResponseException(HttpStatus.SC_NOT_FOUND, error);
            }
            throw new HttpResponseException(HttpStatus.SC_INTERNAL_SERVER_ERROR, error);
        }
    }

    @Override
    protected URIBuilder uriBuilder(String path, Object... pathArgs) {
        return super.uriBuilder(path, pathArgs).setHost("api." + domain);
    }
}
