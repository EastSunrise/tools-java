package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.video.entity.douban.container.BoxResult;
import wsg.tools.internet.video.entity.douban.container.ChartResult;
import wsg.tools.internet.video.entity.douban.container.ContentResult;
import wsg.tools.internet.video.entity.douban.container.RankedResult;
import wsg.tools.internet.video.entity.douban.pojo.*;
import wsg.tools.internet.video.enums.CityEnum;

import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;

/**
 * Extension of {@link DoubanSite} with api-related methods.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public class ApiDoubanSite extends DoubanSite {

    private final String apiKey;

    public ApiDoubanSite(String apiKey) {
        super();
        this.apiKey = apiKey;
    }

    /**
     * This method can't obtain x-rated subjects probably which are restricted to be accessed only after logging in.
     */
    public Subject apiMovieSubject(long subjectId) throws HttpResponseException {
        return getApiObject(buildPath("/v2/movie/subject/%d", subjectId), Subject.class);
    }

    public ImdbInfo apiMovieImdb(String imdbId) throws HttpResponseException {
        return getApiObject(buildPath("/v2/movie/imdb/%s", imdbId), ImdbInfo.class);
    }

    public Celebrity apiMovieCelebrity(long celebrityId) throws HttpResponseException {
        return getApiObject(buildPath("/v2/movie/celebrity/%d", celebrityId), Celebrity.class);
    }

    /**
     * It's updated every Friday.
     */
    public RankedResult apiMovieWeekly() throws HttpResponseException {
        return getApiObject(buildPath("/v2/movie/weekly"), RankedResult.class, false);
    }

    /**
     * It's updated every Friday.
     */
    public BoxResult apiMovieUsBox() throws HttpResponseException {
        return getApiObject(buildPath("/v2/movie/us_box"), BoxResult.class, true);
    }

    public Pair<String, List<SimpleSubject>> apiMovieTop250() throws HttpResponseException {
        return getApiChart(buildPath("/v2/movie/top250"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieNewMovies() throws HttpResponseException {
        return getApiChart(buildPath("/v2/movie/new_movies"));
    }

    public Pair<String, List<SimpleSubject>> apiMovieInTheaters(CityEnum city) throws HttpResponseException {
        return getApiChart(buildPath("/v2/movie/in_theaters").addParameter("city", city.getPath()));
    }

    public Pair<String, List<SimpleSubject>> apiMovieComingSoon() throws HttpResponseException {
        return getApiChart(buildPath("/v2/movie/coming_soon"));
    }

    /**
     * Get chart list of subjects.
     *
     * @return pair of title-subjects
     */
    private Pair<String, List<SimpleSubject>> getApiChart(URIBuilder builder) throws HttpResponseException {
        int start = 0;
        List<SimpleSubject> subjects = new LinkedList<>();
        String title;
        builder.addParameter("count", String.valueOf(MAX_COUNT_ONCE));
        while (true) {
            builder.setParameter("start", String.valueOf(start));
            ChartResult chartResult = getApiObject(builder, ChartResult.class, false);
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
    public Pair<SimpleSubject, List<Photo>> apiMovieSubjectPhotos(long subjectId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/photos", subjectId);
    }

    public Pair<SimpleSubject, List<Review>> apiMovieSubjectReviews(long subjectId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/reviews", subjectId);
    }

    public Pair<SimpleSubject, List<Comment>> apiMovieSubjectComments(long subjectId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/subject/%d/comments", subjectId);
    }

    public Pair<SimpleCelebrity, List<Photo>> apiMovieCelebrityPhotos(long celebrityId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/celebrity/%d/photos", celebrityId);
    }

    public Pair<SimpleCelebrity, List<Work>> apiMovieCelebrityWorks(long celebrityId) throws HttpResponseException {
        return getApiContent(new TypeReference<>() {}, "/v2/movie/celebrity/%d/works", celebrityId);
    }

    /**
     * Get content of a subject or a celebrity, including its owner.
     *
     * @return pair of owner-content
     */
    private <O, C> Pair<O, List<C>> getApiContent(TypeReference<ContentResult<O, C>> type, String path, long ownerId) throws HttpResponseException {
        int start = 0;
        List<C> content = new LinkedList<>();
        O owner;
        while (true) {
            URIBuilder builder = buildPath(path, ownerId)
                    .addParameter("start", String.valueOf(start))
                    .addParameter("count", String.valueOf(MAX_COUNT_ONCE))
                    .addParameter("apikey", apiKey);
            ContentResult<O, C> contentResult;
            try {
                contentResult = getObject(builder.build(), type);
            } catch (URISyntaxException e) {
                throw AssertUtils.runtimeException(e);
            }
            content.addAll(contentResult.getContent());
            owner = contentResult.getOwner();
            start += contentResult.getCount();
            if (start >= contentResult.getTotal()) {
                break;
            }
        }
        return Pair.of(owner, content);
    }

    private <T> T getApiObject(URIBuilder builder, Class<T> clazz) throws HttpResponseException {
        return getApiObject(builder, clazz, true);
    }

    /**
     * Obtains an object from api. Append api key after the uri.
     *
     * @param <T> type of object
     * @return target object
     */
    private <T> T getApiObject(URIBuilder builder, Class<T> clazz, boolean cached) throws HttpResponseException {
        try {
            return getObject(builder.setParameter("apikey", apiKey).build(), clazz, cached);
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
    }

    @Override
    protected URIBuilder buildPath(String path, Object... pathArgs) {
        return super.buildPath(path, pathArgs).setHost("api." + domain);
    }
}
