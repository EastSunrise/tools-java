package wsg.tools.internet.movie.douban.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import wsg.tools.common.jackson.deserializer.AkaEnumDeserializer;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.impl.RequestBuilder;
import wsg.tools.internet.base.intf.SnapshotStrategy;
import wsg.tools.internet.enums.DomesticCity;
import wsg.tools.internet.enums.Language;
import wsg.tools.internet.enums.Region;
import wsg.tools.internet.movie.common.enums.DoubanSubtype;
import wsg.tools.internet.movie.douban.DoubanSite;
import wsg.tools.internet.movie.douban.api.container.BoxResult;
import wsg.tools.internet.movie.douban.api.container.ChartResult;
import wsg.tools.internet.movie.douban.api.container.ContentResult;
import wsg.tools.internet.movie.douban.api.container.RankedResult;
import wsg.tools.internet.movie.douban.api.pojo.Celebrity;
import wsg.tools.internet.movie.douban.api.pojo.Comment;
import wsg.tools.internet.movie.douban.api.pojo.ImdbInfo;
import wsg.tools.internet.movie.douban.api.pojo.Photo;
import wsg.tools.internet.movie.douban.api.pojo.Review;
import wsg.tools.internet.movie.douban.api.pojo.SimpleCelebrity;
import wsg.tools.internet.movie.douban.api.pojo.SimpleSubject;
import wsg.tools.internet.movie.douban.api.pojo.Subject;
import wsg.tools.internet.movie.douban.api.pojo.Work;

/**
 * Extension of {@link DoubanSite} with api-related methods.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ApiDoubanSite extends DoubanSite {

    static {
        MAPPER.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
            .registerModule(new SimpleModule()
                .addDeserializer(Language.class,
                    new AkaEnumDeserializer<>(String.class, Language.class))
                .addDeserializer(Region.class,
                    new AkaEnumDeserializer<>(String.class, Region.class))
                .addDeserializer(DoubanSubtype.class,
                    new AkaEnumDeserializer<>(String.class, DoubanSubtype.class)));
    }

    private final String apikey;

    public ApiDoubanSite(String apikey) {
        super();
        this.apikey = AssertUtils.requireNotBlank(apikey);
    }

    /**
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only
     * after logging in.
     */
    public Subject apiMovieSubject(long subjectId) throws HttpResponseException {
        return getObject(apiBuilder("/v2/movie/subject/%d", subjectId), Subject.class);
    }

    public ImdbInfo apiMovieImdb(String imdbId) throws HttpResponseException {
        return getObject(apiBuilder("/v2/movie/imdb/%s", imdbId), ImdbInfo.class);
    }

    public Celebrity apiMovieCelebrity(long celebrityId) throws HttpResponseException {
        return getObject(apiBuilder("/v2/movie/celebrity/%d", celebrityId), Celebrity.class);
    }

    /**
     * It's updated every Friday.
     */
    public RankedResult apiMovieWeekly() throws HttpResponseException {
        return getObject(apiBuilder("/v2/movie/weekly"), RankedResult.class,
            SnapshotStrategy.always());
    }

    /**
     * It's updated every Friday.
     */
    public BoxResult apiMovieUsBox() throws HttpResponseException {
        return getObject(apiBuilder("/v2/movie/us_box"), BoxResult.class,
            SnapshotStrategy.always());
    }

    public Pair<String, List<SimpleSubject>> apiMovieTop250() throws HttpResponseException {
        return getApiChart(apiBuilder("/v2/movie/top250"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieNewMovies() throws HttpResponseException {
        return getApiChart(apiBuilder("/v2/movie/new_movies"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieInTheaters(DomesticCity city)
        throws HttpResponseException {
        return getApiChart(
            apiBuilder("/v2/movie/in_theaters").addParameter("city", city.getText()));
    }

    public Pair<String, List<SimpleSubject>> apiMovieComingSoon() throws HttpResponseException {
        return getApiChart(apiBuilder("/v2/movie/coming_soon"));
    }

    /**
     * Get chart list of subjects.
     *
     * @return pair of title-subjects
     */
    private Pair<String, List<SimpleSubject>> getApiChart(RequestBuilder builder)
        throws HttpResponseException {
        int start = 0;
        List<SimpleSubject> subjects = new LinkedList<>();
        String title;
        builder.addParameter("count", MAX_COUNT_ONCE);
        while (true) {
            builder.addParameter("start", start);
            ChartResult chartResult = getObject(builder, ChartResult.class,
                SnapshotStrategy.always());
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
    public Pair<SimpleSubject, List<Photo>> apiMovieSubjectPhotos(long subjectId)
        throws HttpResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/subject/%d/photos", subjectId);
    }

    public Pair<SimpleSubject, List<Review>> apiMovieSubjectReviews(long subjectId)
        throws HttpResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/subject/%d/reviews", subjectId);
    }

    public Pair<SimpleSubject, List<Comment>> apiMovieSubjectComments(long subjectId)
        throws HttpResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/subject/%d/comments", subjectId);
    }

    public Pair<SimpleCelebrity, List<Photo>> apiMovieCelebrityPhotos(long celebrityId)
        throws HttpResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/celebrity/%d/photos", celebrityId);
    }

    public Pair<SimpleCelebrity, List<Work>> apiMovieCelebrityWorks(long celebrityId)
        throws HttpResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/celebrity/%d/works", celebrityId);
    }

    /**
     * Get content of a subject or a celebrity, including its owner.
     *
     * @return pair of owner-content
     */
    private <O, C> Pair<O, List<C>> getApiContent(TypeReference<ContentResult<O, C>> type,
        String path, long ownerId)
        throws HttpResponseException {
        int start = 0;
        List<C> content = new LinkedList<>();
        O owner;
        while (true) {
            RequestBuilder builder = apiBuilder(path, ownerId)
                .addParameter("start", start)
                .addParameter("count", MAX_COUNT_ONCE);
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

    private <T> T getObject(RequestBuilder builder, Class<T> clazz) throws HttpResponseException {
        return getObject(builder, clazz, SnapshotStrategy.never());
    }

    private <T> T getObject(RequestBuilder builder, TypeReference<T> type)
        throws HttpResponseException {
        builder.setToken("apikey", apikey);
        return getObject(builder, MAPPER, type, SnapshotStrategy.never());
    }

    private <T> T getObject(RequestBuilder builder, Class<T> clazz, SnapshotStrategy<T> strategy)
        throws HttpResponseException {
        builder.setToken("apikey", apikey);
        return getObject(builder, MAPPER, clazz, strategy);
    }

    private RequestBuilder apiBuilder(String path, Object... pathArgs) {
        return builder("api", path, pathArgs);
    }
}