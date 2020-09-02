package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.MarkEnum;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Obtains info from <a href="https://douban.com">豆瓣</a>.
 * <p>
 * An api key is required.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
public class DoubanSite extends BaseSite {

    public static final LocalDate DOUBAN_START_DATE = LocalDate.of(2005, 3, 6);
    protected static final int MAX_COUNT_ONCE = 100;
    private static final Pattern MOVIE_API_ALT_REGEX = Pattern.compile("https://api.douban.com/movie/(\\d+)/?");
    private static final Pattern IMDB_URL_REGEX = Pattern.compile("https://www.imdb.com/title/(tt\\d+)");
    private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern.compile("[^()\\s]+\\((\\d+)\\)");
    private static final Pattern COLLECTIONS_PAGE_REGEX = Pattern.compile("(\\d+)-(\\d+)\\s/\\s(\\d+)");

    public DoubanSite() {
        super("Douban", "douban.com", 1);
    }

    @Override
    protected void setObjectMapper() {
        super.setObjectMapper();
        objectMapper.registerModule(new SimpleModule()
                .addDeserializer(GenreEnum.class, EnumDeserializers.getTitleDeserializer(GenreEnum.class))
        ).registerModule(new JavaTimeModule()
                .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.STANDARD_DATE_TIME_FORMATTER))
        );
    }

    /**
     * Returns marked subjects of the given user since the given start date.
     *
     * @param catalog movie/book/music/...
     * @param mark    wish/do/collect
     * @return map of (id, mark date)
     */
    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, CatalogEnum catalog, MarkEnum mark) throws HttpResponseException {
        if (since == null) {
            since = DOUBAN_START_DATE;
        }
        log.info("Collect {} of user {} since {}", catalog, userId, since);
        Map<Long, LocalDate> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int start = 0;
        while (true) {
            URI uri;
            try {
                uri = buildCatalogPath("/people/%d/%s", catalog, userId, mark.getPath())
                        .addParameter("sort", "time")
                        .addParameter("start", String.valueOf(start))
                        .addParameter("mode", "list").build();
            } catch (URISyntaxException e) {
                throw AssertUtils.runtimeException(e);
            }
            Document document = getDocument(uri, false);
            boolean done = false;
            String listClass = ".list-view";
            for (Element li : document.selectFirst(listClass).select(HTML_LI)) {
                Element div = li.selectFirst(".title");
                String href = div.selectFirst(HTML_A).attr("href");
                long id = Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/"));
                LocalDate markDate = LocalDate.parse(div.nextElementSibling().text().strip());
                if (!markDate.isBefore(since)) {
                    map.put(id, markDate);
                    start++;
                } else {
                    done = true;
                    break;
                }
            }

            String numStr = document.selectFirst("span.subject-num").text().strip();
            Matcher matcher = AssertUtils.matches(COLLECTIONS_PAGE_REGEX, numStr);
            if (start >= Integer.parseInt(matcher.group(3)) || done) {
                break;
            }
        }
        log.info("Collected {} {}", map.size(), catalog);
        return map;
    }

    /**
     * Returns ids of collected creators.
     *
     * @param catalog movie/book/music/...
     */
    public List<Long> collectUserCreators(long userId, CatalogEnum catalog) throws HttpResponseException {
        log.info("Collect {} of user {}", catalog.getCreator().getPath(), userId);
        List<Long> ids = new LinkedList<>();
        int start = 0;
        while (true) {
            URI uri;
            try {
                uri = buildCatalogPath("/people/%d/%s", catalog, userId, catalog.getCreator().getPath())
                        .addParameter("start", String.valueOf(start)).build();
            } catch (URISyntaxException e) {
                throw AssertUtils.runtimeException(e);
            }
            Document document = getDocument(uri, false);
            String itemClass = ".item";
            for (Element div : document.select(itemClass)) {
                Element a = div.selectFirst(".title").selectFirst(HTML_A);
                String href = a.attr("href");
                ids.add(Long.parseLong(StringUtils.substringAfterLast(StringUtils.strip(href, "/"), "/")));
                start++;
            }
            Element title = document.selectFirst(HTML_TITLE);
            Matcher matcher = AssertUtils.matches(CREATORS_PAGE_TITLE_REGEX, title.text().strip());
            if (start >= Integer.parseInt(matcher.group(1))) {
                break;
            }
        }
        log.info("Collected {} {}", ids.size(), catalog.getCreator().getPath());
        return ids;
    }

    /**
     * Parse the page of a subject to get its info.
     * <p>
     * This method can't obtain x-rated subject probably.
     *
     * @return IMDb id
     */
    public BaseDoubanSubject subject(long subjectId) throws HttpResponseException {
        Document document;
        try {
            document = getDocument(buildCatalogPath("/subject/%d", CatalogEnum.MOVIE, subjectId).build());
        } catch (URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        String text = document.selectFirst("script[type=application/ld+json]").html();
        text = StringUtils.replaceChars(text, "\n\t", "");
        BaseDoubanSubject subject;
        try {
            subject = objectMapper.readValue(text, BaseDoubanSubject.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }

        Element info = document.selectFirst("div#info");
        Matcher matcher = IMDB_URL_REGEX.matcher(info.html());
        if (matcher.find()) {
            subject.setImdbId(matcher.group(1));
        }
        return subject;
    }

    private URIBuilder buildCatalogPath(String path, CatalogEnum catalog, Object... pathArgs) {
        return super.buildPath(path, pathArgs).setHost(catalog.getPath() + "." + domain);
    }
}
