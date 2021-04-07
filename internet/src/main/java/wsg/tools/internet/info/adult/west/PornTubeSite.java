package wsg.tools.internet.info.adult.west;

import java.io.IOException;
import java.net.URL;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.net.NetUtils;
import wsg.tools.common.util.TimeUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.base.repository.support.Repositories;
import wsg.tools.internet.base.support.BaseSite;
import wsg.tools.internet.base.support.RequestWrapper;
import wsg.tools.internet.common.AbstractHeaderResponseHandler;
import wsg.tools.internet.common.CssSelectors;
import wsg.tools.internet.common.DocumentUtils;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.common.UnexpectedException;

/**
 * @author Kingen
 * @see <a href="https://porn-tube-club.com/">Porn Tube Club</a>
 * @since 2021/3/17
 */
public class PornTubeSite extends BaseSite implements RepoRetrievable<Integer, PornTubeVideo> {

    private static final String HOME = "https://porn-tube-club.com";
    private static final String HOME_TITLE_PREFIX = "Only HD Porn Video Tube";
    private static final String CATEGORIES_TITLE_PREFIX = "All HD Porn Video Categories";
    private static final long TIMESTAMP = 1617310800;
    private static long timestamp = TIMESTAMP;

    public PornTubeSite() {
        super("Porn Tube Club", httpsHost("porn-tube-club.com"));
    }

    /**
     * Returns the repository of all videos.
     */
    public ListRepository<Integer, PornTubeVideo> getRepository() throws OtherResponseException {
        List<Integer> ids = new ArrayList<>(findAllVideoIndices().keySet());
        return Repositories.list(this, ids);
    }

    /**
     * Returns the repository of all stars.
     */
    public ListRepository<String, PornTubeStar> getStarRepository() throws OtherResponseException {
        return Repositories.list(this::findStarByName, findAllStars());
    }

    /**
     * Retrieves all tags.
     *
     * @see #findAllByTag(PornTubeTag)
     */
    public List<PornTubeTag> findAllTags() throws OtherResponseException {
        Document document = findDocument(httpGet("/porn-tags/"), t -> true);
        Elements as = document.selectFirst(".wpb_wrapper").select(CssSelectors.TAG_A);
        return as.stream().map(a -> {
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.TAG_HREF_REGEX, href);
            String titlePath = matcher.group("p");
            return new PornTubeTag(titlePath, a.text());
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves all indices of videos with the specified tag.
     *
     * @see #findAllTags()
     */
    public List<PornTubeSimpleVideo> findAllByTag(@Nonnull PornTubeTag tag)
        throws NotFoundException, OtherResponseException {
        String path = tag.getAsPath().replace(" ", "+");
        Document document = getDocument(httpGet("/t%d/%s/", TIMESTAMP, path), t -> true);
        return getVideos(document.selectFirst(".wpb_wrapper"));
    }

    /**
     * Retrieves all categories.
     *
     * @see #findPageByCategory(int, PornTubePageReq)
     */
    public List<PornTubeCategory> findAllCategories() throws OtherResponseException {
        Document document = null;
        try {
            document = getDocument(httpGet("/c%d/", TIMESTAMP), t -> true);
        } catch (NotFoundException e) {
            throw new UnexpectedException(e);
        }
        Element wrapper = document.selectFirst(".video-block-container-wrapper");
        return wrapper.select(".more_title").stream().map(h4 -> {
            String title = ((TextNode) h4.childNode(0)).text();
            Element a = h4.selectFirst(".video-category-count");
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CATEGORY_HREF_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            String text = a.text();
            int videos = Integer.parseInt(text.substring(0, text.length() - 7));
            return new PornTubeCategory(id, title, videos);
        }).collect(Collectors.toList());
    }

    /**
     * Retrieve page of videos under the specified category.
     *
     * @see #findAllCategories()
     */
    public PornTubePageResult findPageByCategory(int categoryId, @Nonnull PornTubePageReq req)
        throws NotFoundException, OtherResponseException {
        AssertUtils.requireRange(categoryId, 1, null);
        Element wpb = redirect(categoryId, req.getCurrent() + 1).selectFirst(".wpb_wrapper");
        List<PornTubeSimpleVideo> videos = getVideos(wpb);
        Element last = wpb.select(".page-numbers").last();
        int totalPages;
        if (last.is(CssSelectors.TAG_A)) {
            String href = last.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CATEGORY_HREF_REGEX, href);
            totalPages = Integer.parseInt(matcher.group("p"));
        } else {
            totalPages = Integer.parseInt(last.text());
        }
        return new PornTubePageResult(videos, req, totalPages);
    }

    private List<PornTubeSimpleVideo> getVideos(@Nonnull Element wrapper) throws NotFoundException {
        Elements lis = wrapper.selectFirst(".video-home").children();
        if (lis.isEmpty()) {
            throw new NotFoundException("No videos found in the page.");
        }
        return lis.stream().map(li -> {
            Duration duration = TimeUtils.parseDuration(li.selectFirst(".video-duration").text());
            int views = Integer.parseInt(li.selectFirst(".views").text());
            int likes = Integer.parseInt(li.selectFirst(".likes").text());
            URL thumb = NetUtils.createURL(li.selectFirst(".imgHome").attr(CssSelectors.ATTR_SRC));
            Element a = li.selectFirst(".videoHname");
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VIDEO_HREF_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            return new PornTubeSimpleVideo(id, thumb, a.text(), duration, views, likes);
        }).collect(Collectors.toList());
    }

    /**
     * Retrieves names of all stars.
     *
     * @return names of stars
     * @see #findStarByName(String)
     */
    public List<String> findAllStars() throws OtherResponseException {
        Document document = findDocument(httpGet("/allpornstars"), t -> true);
        Elements as = document.selectFirst(".wpb_wrapper").select(CssSelectors.TAG_A);
        return as.stream().map(Element::text).collect(Collectors.toList());
    }

    /**
     * Retrieves a star of the specific name.
     *
     * @see #findAllStars()
     */
    @Nonnull
    public PornTubeStar findStarByName(@Nonnull String name)
        throws NotFoundException, OtherResponseException {
        String path = name.replace(" ", "-").toLowerCase(Locale.ROOT);
        RequestWrapper wrapper = httpGet("/pornstars/").addParameter("pornstar", path);
        Document document = getDocument(wrapper, t -> true);
        String cover = DocumentUtils.getMetadata(document).get("og:image");
        Elements lis = document.selectFirst(".video_module").children();
        List<PornTubeVideoIndex> videos = lis.stream().map(li -> {
            URL thumb = NetUtils.createURL(li.selectFirst(".related").attr(CssSelectors.ATTR_SRC));
            Element a = li.selectFirst(".videoHname");
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VIDEO_HREF_REGEX, href);
            int id = Integer.parseInt(matcher.group("id"));
            return new PornTubeVideoIndex(id, thumb, a.text());
        }).collect(Collectors.toList());
        return new PornTubeStar(name, NetUtils.createURL(cover), videos);
    }

    /**
     * Retrieves all indices of videos.
     *
     * @return id-title mappings
     * @see #findById(Integer)
     */
    public Map<Integer, String> findAllVideoIndices() throws OtherResponseException {
        Document document = findDocument(httpGet("/sitemap/"), t -> true);
        Elements as = document.selectFirst(".wpb_wrapper").select(CssSelectors.TAG_A);
        return as.stream().collect(Collectors.toMap(a -> {
            String href = a.attr(CssSelectors.ATTR_HREF);
            Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.VIDEO_HREF_REGEX, href);
            return Integer.parseInt(matcher.group("id"));
        }, Element::text));
    }

    /**
     * Retrieves a video by the given identifier.
     *
     * @see #findAllVideoIndices()
     * @see #findAllByTag(PornTubeTag)
     * @see #findPageByCategory(int, PornTubePageReq)
     * @see PornTubeStar#getVideos()
     */
    @Override
    @Nonnull
    public PornTubeVideo findById(@Nonnull Integer id)
        throws NotFoundException, OtherResponseException {
        RequestWrapper wrapper = httpGet("/v%d/%d", TIMESTAMP, id);
        Pair<Map<String, List<Header>>, Document> pair = null;
        try {
            pair = execute(wrapper.getHttpHost(), wrapper.build(),
                new PornTubeResponseHandler());
        } catch (HttpResponseException e) {
            if (e.getStatusCode() == HttpStatus.SC_NOT_FOUND) {
                throw new NotFoundException(e.getMessage());
            }
            throw new OtherResponseException(e);
        }
        Document document = pair.getRight();
        if (document.title().startsWith(HOME_TITLE_PREFIX)) {
            throw new NotFoundException("Not found video");
        }
        Map<String, String> metadata = DocumentUtils.getMetadata(document);
        URL thumb = NetUtils.createURL(metadata.get("og:image:secure_url"));
        URL ogVideo = NetUtils.createURL(metadata.get("og:video"));
        String title = metadata.get("og:title");
        String description = metadata.get("og:description");
        String source = document.selectFirst("#myvideo").child(0).attr(CssSelectors.ATTR_SRC);
        Element content = document.selectFirst(".entry-content");
        Duration duration = TimeUtils.parseDuration(content.selectFirst(".video-duration").text());
        int views = Integer.parseInt(content.selectFirst(".views").text());
        int likes = Integer.parseInt(content.selectFirst(".open_video_likes_count").text());
        String postDate = content.selectFirst(".post_date").text();
        LocalDateTime postTime = getUpdateTime(pair.getLeft(), postDate);
        PornTubeVideo video = new PornTubeVideo(id, thumb, title, duration, views, likes,
            ogVideo, description, NetUtils.createURL(source), postTime);
        Elements divs = content.select(".video_category");
        video.setCategories(divs.first().select(CssSelectors.TAG_A).stream()
            .collect(Collectors.toMap(a -> {
                String href = a.attr(CssSelectors.ATTR_HREF);
                Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.CATEGORY_HREF_REGEX, href);
                return Integer.parseInt(matcher.group("id"));
            }, Element::text)));
        video.setTags(divs.last().select(CssSelectors.TAG_A).stream()
            .map(a -> {
                String href = a.attr(CssSelectors.ATTR_HREF);
                String path = RegexUtils.matchesOrElseThrow(Lazy.TAG_HREF_REGEX, href).group("p");
                return new PornTubeTag(path, a.text());
            }).collect(Collectors.toList()));
        return video;
    }

    private LocalDateTime getUpdateTime(Map<String, List<Header>> headers, String text) {
        String date = headers.get("Date").get(0).getElements()[1].getName();
        LocalDateTime now = LocalDateTime.parse(date, Lazy.FORMATTER);
        Matcher matcher = RegexUtils.matchesOrElseThrow(Lazy.AGO_REGEX, text);
        return now.minusYears(Integer.parseInt(matcher.group("y")))
            .minusMonths(Integer.parseInt(matcher.group("M")))
            .minusDays(Integer.parseInt(matcher.group("d")))
            .minusHours(Integer.parseInt(matcher.group("h")))
            .minusMinutes(Integer.parseInt(matcher.group("m")))
            .minusSeconds(Integer.parseInt(matcher.group("s")));
    }

    private Document redirect(int categoryId, int page)
        throws OtherResponseException, NotFoundException {
        RequestWrapper wrapper = httpGet("/c%d/c/%d/p/%d/", timestamp, categoryId, page);
        Document document = getDocument(wrapper, t -> true);
        String title = document.title();
        if (!title.startsWith(CATEGORIES_TITLE_PREFIX)) {
            return document;
        }
        timestamp = Long.parseLong(title.substring(52));
        wrapper = httpGet("/c%d/c/%d/p/%d/", timestamp, categoryId, page);
        return getDocument(wrapper, t -> true);
    }

    private static class Lazy {

        private static final Pattern VIDEO_HREF_REGEX = Pattern
            .compile(HOME + "/v\\d{10}/(?<id>\\d+)/");
        private static final Pattern CATEGORY_HREF_REGEX = Pattern
            .compile(HOME + "/c\\d{10}/c/(?<id>\\d+)/p/(?<p>\\d+)/");
        private static final Pattern TAG_HREF_REGEX = Pattern
            .compile(HOME + "/t\\d{10}/(?<p>[A-Za-z\\d.+ -]+)/");
        private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd MMM yyyy HH:mm:ss z").localizedBy(Locale.US);
        private static final Pattern AGO_REGEX = Pattern
            .compile("(?<y>\\d) years, (?<M>\\d+) months, (?<d>\\d+) days, "
                + "(?<h>\\d+) hours, (?<m>\\d+) minutes, (?<s>\\d+) seconds ago");
    }

    private static class PornTubeResponseHandler extends AbstractHeaderResponseHandler<Document> {

        @Override
        public Document handleEntity(HttpEntity entity) throws IOException {
            return Jsoup.parse(EntityUtils.toString(entity, Constants.UTF_8));
        }
    }
}
