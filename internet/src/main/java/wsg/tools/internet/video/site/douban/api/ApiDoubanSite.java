package wsg.tools.internet.video.site.douban.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.protocol.HttpContext;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.RequestBuilder;
import wsg.tools.internet.base.SnapshotStrategy;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.video.enums.CityEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.RegionEnum;
import wsg.tools.internet.video.enums.SubtypeEnum;
import wsg.tools.internet.video.site.douban.DoubanSite;
import wsg.tools.internet.video.site.douban.api.container.BoxResult;
import wsg.tools.internet.video.site.douban.api.container.ChartResult;
import wsg.tools.internet.video.site.douban.api.container.ContentResult;
import wsg.tools.internet.video.site.douban.api.container.RankedResult;
import wsg.tools.internet.video.site.douban.api.pojo.*;

import java.util.LinkedList;
import java.util.List;

/**
 * Extension of {@link DoubanSite} with api-related methods.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ApiDoubanSite extends DoubanSite {

    private final String apikey;

    public ApiDoubanSite(String apikey) {
        super();
        this.apikey = apikey;
    }

    @Override
    protected ObjectMapper objectMapper() {
        return super.objectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .registerModule(new SimpleModule()
                        .addDeserializer(LanguageEnum.class, EnumDeserializers.getAkaDeserializer(String.class, LanguageEnum.class))
                        .addDeserializer(RegionEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RegionEnum.class))
                        .addDeserializer(SubtypeEnum.class, EnumDeserializers.getAkaDeserializer(String.class, SubtypeEnum.class))
                );
    }

    /**
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only after logging in.
     */
    public Subject apiMovieSubject(long subjectId) throws NotFoundException {
        return getObject(uriBuilder("/v2/movie/subject/%d", subjectId), Subject.class);
    }

    public ImdbInfo apiMovieImdb(String imdbId) throws NotFoundException {
        return getObject(uriBuilder("/v2/movie/imdb/%s", imdbId), ImdbInfo.class);
    }

    public Celebrity apiMovieCelebrity(long celebrityId) throws NotFoundException {
        return getObject(uriBuilder("/v2/movie/celebrity/%d", celebrityId), Celebrity.class);
    }

    /**
     * It's updated every Friday.
     */
    public RankedResult apiMovieWeekly() {
        try {
            return getObject(uriBuilder("/v2/movie/weekly"), RankedResult.class, SnapshotStrategy.ALWAYS_UPDATE);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    /**
     * It's updated every Friday.
     */
    public BoxResult apiMovieUsBox() {
        try {
            return getObject(uriBuilder("/v2/movie/us_box"), BoxResult.class, SnapshotStrategy.NEVER_UPDATE);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    public Pair<String, List<SimpleSubject>> apiMovieTop250() {
        return getApiChart(uriBuilder("/v2/movie/top250"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieNewMovies() {
        return getApiChart(uriBuilder("/v2/movie/new_movies"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieInTheaters(CityEnum city) {
        return getApiChart(uriBuilder("/v2/movie/in_theaters").addParameter("city", city.getPath()));
    }

    public Pair<String, List<SimpleSubject>> apiMovieComingSoon() {
        return getApiChart(uriBuilder("/v2/movie/coming_soon"));
    }

    /**
     * Get chart list of subjects.
     *
     * @return pair of title-subjects
     */
    private Pair<String, List<SimpleSubject>> getApiChart(URIBuilder builder) {
        int start = 0;
        List<SimpleSubject> subjects = new LinkedList<>();
        String title;
        builder.addParameter("count", String.valueOf(MAX_COUNT_ONCE));
        while (true) {
            builder.setParameter("start", String.valueOf(start));
            ChartResult chartResult;
            try {
                chartResult = getObject(builder, ChartResult.class, SnapshotStrategy.ALWAYS_UPDATE);
            } catch (NotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
            subjects.addAll(chartResult.getContent());
            title = chartResult.getTitle();
            start += chartResult.getCount();
            if (start >= chartResult.getTotal()) {
                break;
            }
        }
        return Pair.of(title, subjects);
    }

    /**
     * Only include official photos.
     */
    public Pair<SimpleSubject, List<Photo>> apiMovieSubjectPhotos(long subjectId) throws NotFoundException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/photos", subjectId);
    }

    public Pair<SimpleSubject, List<Review>> apiMovieSubjectReviews(long subjectId) throws NotFoundException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/reviews", subjectId);
    }

    public Pair<SimpleSubject, List<Comment>> apiMovieSubjectComments(long subjectId) throws NotFoundException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/comments", subjectId);
    }

    public Pair<SimpleCelebrity, List<Photo>> apiMovieCelebrityPhotos(long celebrityId) throws NotFoundException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/celebrity/%d/photos", celebrityId);
    }

    public Pair<SimpleCelebrity, List<Work>> apiMovieCelebrityWorks(long celebrityId) throws NotFoundException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/celebrity/%d/works", celebrityId);
    }

    /**
     * Get content of a subject or a celebrity, including its owner.
     *
     * @return pair of owner-content
     */
    private <O, C> Pair<O, List<C>> getApiContent(TypeReference<ContentResult<O, C>> type, String path, long ownerId)
            throws NotFoundException {
        int start = 0;
        List<C> content = new LinkedList<>();
        O owner;
        while (true) {
            URIBuilder builder = uriBuilder(path, ownerId)
                    .addParameter("start", String.valueOf(start))
                    .addParameter("count", String.valueOf(MAX_COUNT_ONCE));
            ContentResult<O, C> contentResult = getObject(builder, type);
            content.addAll(contentResult.getContent());
            owner = contentResult.getOwner();
            start += contentResult.getCount();
            if (start >= contentResult.getTotal()) {
                break;
            }
        }
        return Pair.of(owner, content);
    }

    @Override
    public void handleRequest(RequestBuilder builder, HttpContext context) {
        builder.addToken("apikey", apikey);
    }

    private URIBuilder uriBuilder(String path, Object... pathArgs) {
        return super.builder("api", path, pathArgs);
    }
}