package wsg.tools.internet.movie.douban.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.methods.RequestBuilder;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.internet.base.ResponseWrapper;
import wsg.tools.internet.base.WrappedResponseHandler;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.enums.DomesticCity;
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

    private final String apikey;

    public ApiDoubanSite(String apikey) {
        super("api");
        this.apikey = AssertUtils.requireNotBlank(apikey);
    }

    /**
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only
     * after logging in.
     */
    public Subject apiMovieSubject(long subjectId)
        throws NotFoundException, OtherResponseException {
        return getObject(httpGet("/v2/movie/subject/%d", subjectId), Subject.class);
    }

    public ImdbInfo apiMovieImdb(String imdbId) throws NotFoundException, OtherResponseException {
        return getObject(httpGet("/v2/movie/imdb/%s", imdbId), ImdbInfo.class);
    }

    public Celebrity apiMovieCelebrity(long celebrityId)
        throws NotFoundException, OtherResponseException {
        return getObject(httpGet("/v2/movie/celebrity/%d", celebrityId), Celebrity.class);
    }

    /**
     * It's updated every Friday.
     */
    public RankedResult apiMovieWeekly() throws NotFoundException, OtherResponseException {
        return getObject(httpGet("/v2/movie/weekly"), RankedResult.class);
    }

    /**
     * It's updated every Friday.
     */
    public BoxResult apiMovieUsBox() throws NotFoundException, OtherResponseException {
        return getObject(httpGet("/v2/movie/us_box"), BoxResult.class);
    }

    public Pair<String, List<SimpleSubject>> apiMovieTop250()
        throws NotFoundException, OtherResponseException {
        return getApiChart(httpGet("/v2/movie/top250"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieNewMovies()
        throws NotFoundException, OtherResponseException {
        return getApiChart(httpGet("/v2/movie/new_movies"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieInTheaters(DomesticCity city)
        throws NotFoundException, OtherResponseException {
        return getApiChart(httpGet("/v2/movie/in_theaters").addParameter("city", city.getEnName()));
    }

    public Pair<String, List<SimpleSubject>> apiMovieComingSoon()
        throws NotFoundException, OtherResponseException {
        return getApiChart(httpGet("/v2/movie/coming_soon"));
    }

    /**
     * Get chart list of subjects.
     *
     * @return pair of title-subjects
     */
    private Pair<String, List<SimpleSubject>> getApiChart(RequestBuilder builder)
        throws NotFoundException, OtherResponseException {
        int start = 0;
        List<SimpleSubject> subjects = new LinkedList<>();
        String title;
        builder.addParameter("count", String.valueOf(MAX_COUNT_ONCE));
        while (true) {
            builder.addParameter("start", String.valueOf(start));
            ChartResult chartResult = getObject(builder, ChartResult.class);
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
        throws NotFoundException, OtherResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/subject/%d/photos", subjectId);
    }

    public Pair<SimpleSubject, List<Review>> apiMovieSubjectReviews(long subjectId)
        throws NotFoundException, OtherResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/subject/%d/reviews", subjectId);
    }

    public Pair<SimpleSubject, List<Comment>> apiMovieSubjectComments(long subjectId)
        throws NotFoundException, OtherResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/subject/%d/comments", subjectId);
    }

    public Pair<SimpleCelebrity, List<Photo>> apiMovieCelebrityPhotos(long celebrityId)
        throws NotFoundException, OtherResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/celebrity/%d/photos", celebrityId);
    }

    public Pair<SimpleCelebrity, List<Work>> apiMovieCelebrityWorks(long celebrityId)
        throws NotFoundException, OtherResponseException {
        return getApiContent(new TypeReference<>() {
        }, "/v2/movie/celebrity/%d/works", celebrityId);
    }

    /**
     * Get content of a subject or a celebrity, including its owner.
     *
     * @return pair of owner-content
     */
    private <O, C> Pair<O, List<C>> getApiContent(TypeReference<ContentResult<O, C>> type,
        String path, long ownerId) throws NotFoundException, OtherResponseException {
        int start = 0;
        List<C> content = new LinkedList<>();
        O owner;
        while (true) {
            RequestBuilder builder = httpGet(path, ownerId)
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

    private <T> T getObject(@Nonnull RequestBuilder builder, TypeReference<T> type)
        throws NotFoundException, OtherResponseException {
        return getObject(builder, Lazy.MAPPER, type);
    }

    private <T> T getObject(RequestBuilder builder, Class<T> clazz)
        throws NotFoundException, OtherResponseException {
        return getObject(builder, Lazy.MAPPER, clazz);
    }

    @Override
    public <T> ResponseWrapper<T> getResponseWrapper(@Nonnull RequestBuilder builder,
        WrappedResponseHandler<T> responseHandler) throws IOException {
        builder.addParameter("apikey", apikey);
        return super.getResponseWrapper(builder, responseHandler);
    }

    private static class Lazy {

        private static final ObjectMapper MAPPER = new ObjectMapper();
    }
}
