package wsg.tools.internet.video.site;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import wsg.tools.common.constant.Constants;
import wsg.tools.common.constant.SignConstants;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.video.entity.douban.base.BaseDoubanSubject;
import wsg.tools.internet.video.entity.douban.base.BaseSuggestItem;
import wsg.tools.internet.video.entity.douban.object.DoubanSeries;
import wsg.tools.internet.video.entity.douban.object.SearchItem;
import wsg.tools.internet.video.enums.CatalogEnum;
import wsg.tools.internet.video.enums.GenreEnum;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.enums.MarkEnum;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Obtains info from <a href="https://douban.com">豆瓣</a>.
 *
 * @author Kingen
 * @since 2020/6/15
 */
@Slf4j
public class DoubanSite extends BaseSite {

    public static final LocalDate DOUBAN_START_DATE = LocalDate.of(2005, 3, 6);
    protected static final int MAX_COUNT_ONCE = 100;
    protected static final int COUNT_PER_PAGE = 15;
    private static final Pattern SUBJECT_URL_REGEX = Pattern.compile("https://movie.douban.com/subject/(\\d{7,8})/?");
    private static final Pattern IMDB_URL_REGEX = Pattern.compile("https://www.imdb.com/title/(tt\\d+)");
    private static final Pattern CREATORS_PAGE_TITLE_REGEX = Pattern.compile("[^()\\s]+\\((\\d+)\\)");
    private static final Pattern PAGE_TITLE_REGEX = Pattern.compile("(.*)\\s\\(豆瓣\\)");
    private static final Pattern COLLECTIONS_PAGE_REGEX = Pattern.compile("(\\d+)-(\\d+)\\s/\\s(\\d+)");

    public DoubanSite() {
        super("Douban", "douban.com", 1);
    }

    @Override
    protected ObjectMapper objectMapper() {
        return super.objectMapper()
                .registerModule(new SimpleModule()
                        .addDeserializer(GenreEnum.class, EnumDeserializers.getTitleDeserializer(GenreEnum.class))
                ).registerModule(new JavaTimeModule()
                        .addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(Constants.STANDARD_DATE_TIME_FORMATTER))
                );
    }

    /**
     * Parse the page of a subject to get its info.
     * <p>
     * This method can't obtain x-rated subject probably.
     *
     * @return IMDb id
     */
    public BaseDoubanSubject subject(long subjectId) throws IOException {
        Document document = getDocument(buildCatalogPath(CatalogEnum.MOVIE, "/subject/%d", subjectId));
        String text = document.selectFirst("script[type=application/ld+json]").html();
        text = StringUtils.replaceChars(text, "\n\t", "");
        BaseDoubanSubject subject;
        try {
            subject = objectMapper.readValue(text, BaseDoubanSubject.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }

        String title = AssertUtils.matches(PAGE_TITLE_REGEX, document.title()).group(1);
        subject.setTitle(title);
        String name = subject.getName().replace("  ", " ");
        if (name.startsWith(title)) {
            if (name.length() > title.length()) {
                subject.setOriginalTitle(name.substring(title.length()));
            }
        } else {
            throw new HttpResponseException(HttpStatus.SC_EXPECTATION_FAILED, "Name and title are not matched.");
        }

        String year = StringUtils.strip(document.selectFirst("span.year").html(), "()");
        subject.setYear(Year.of(Integer.parseInt(year)));

        Element info = document.selectFirst("div#info");
        Map<String, Element> spans = info.select("span.pl").stream().collect(Collectors.toMap(Element::text, span -> span));
        Element span;

        final String plLanguage = "语言:";
        if ((span = spans.get(plLanguage)) != null) {
            String[] languages = ((TextNode) span.nextSibling()).text().split(SignConstants.SLASH);
            subject.setLanguages(Arrays.stream(languages).map(
                    language -> EnumUtilExt.deserializeAka(language.strip(), LanguageEnum.class)
            ).collect(Collectors.toList()));
        }
        final String plImdb = "IMDb链接:";
        if ((span = spans.get(plImdb)) != null) {
            String href = span.nextElementSibling().attr("href");
            subject.setImdbId(AssertUtils.matches(IMDB_URL_REGEX, href).group(1));
        }

        if (subject instanceof DoubanSeries) {
            DoubanSeries series = (DoubanSeries) subject;
            final String plEpisodes = "集数:";
            if ((span = spans.get(plEpisodes)) != null) {
                series.setEpisodesCount(Integer.parseInt(((TextNode) span.nextSibling()).text().strip()));
            }
        }

        return subject;
    }

    /**
     * Obtains id of Douban by searching id of IMDb.
     */
    public Long getDbIdByImdbId(String imdbId) {
        List<SearchItem> items = search(CatalogEnum.MOVIE, imdbId, 0);
        if (items.size() == 0) {
            return null;
        }
        if (items.size() == 1) {
            SearchItem item = items.get(0);
            Matcher matcher = AssertUtils.matches(SUBJECT_URL_REGEX, item.getHref());
            return Long.parseLong(matcher.group(1));
        }
        throw new RuntimeException("More than one items returned when searching by id of IMDb.");
    }

    /**
     * Fuzzy search by specified keyword.
     *
     * @param catalog which catalog
     * @param keyword not blank
     * @param page    start with 0. Negative number is regarded as 0.
     */
    public List<SearchItem> search(CatalogEnum catalog, String keyword, int page) {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        URIBuilder builder = uriBuilder("/%s/subject_search", catalog.getPath())
                .setHost("search." + domain)
                .setParameter("search_text", keyword)
                .setParameter("cat", String.valueOf(catalog.getCode()));
        if (page > 0) {
            builder.setParameter("start", String.valueOf(page * COUNT_PER_PAGE));
        }
        Document document;
        try {
            document = getDocument(builder, true, true);
        } catch (IOException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements items = document.select("div.item-root");
        List<SearchItem> result = new ArrayList<>();
        for (Element item : items) {
            Element a = item.selectFirst("a.title-text");
            SearchItem searchItem = new SearchItem();
            searchItem.setTitle(a.text().strip());
            searchItem.setHref(a.attr(HTML_HREF));
            result.add(searchItem);
        }
        return result;
    }

    /**
     * Suggested by keyword.
     * <p>
     * Attention: items are not returned every time since non-null response may have delay.
     *
     * @param catalog which catalog
     * @param keyword not blank
     */
    public List<BaseSuggestItem> suggest(CatalogEnum catalog, String keyword) throws IOException {
        if (StringUtils.isBlank(keyword)) {
            throw new IllegalArgumentException("Keyword mustn't be blank.");
        }
        URIBuilder builder = buildCatalogPath(catalog, "/j/subject_suggest")
                .setParameter("q", keyword);
        return getObject(builder, new TypeReference<>() {}, false);
    }

    /**
     * Returns marked subjects of the given user since the given start date.
     *
     * @param catalog movie/book/music/...
     * @param mark    wish/do/collect
     * @return map of (id, mark date)
     */
    public Map<Long, LocalDate> collectUserSubjects(long userId, LocalDate since, CatalogEnum catalog, MarkEnum mark) throws IOException {
        if (since == null) {
            since = DOUBAN_START_DATE;
        }
        log.info("Collect {} of user {} since {}", catalog, userId, since);
        Map<Long, LocalDate> map = new HashMap<>(Constants.DEFAULT_MAP_CAPACITY);
        int start = 0;
        while (true) {
            URIBuilder builder = buildCatalogPath(catalog, "/people/%d/%s", userId, mark.getPath())
                    .addParameter("sort", "time")
                    .addParameter("start", String.valueOf(start))
                    .addParameter("mode", "list");
            Document document = getDocument(builder, false, false);
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
    public List<Long> collectUserCreators(long userId, CatalogEnum catalog) throws IOException {
        log.info("Collect {} of user {}", catalog.getCreator().getPath(), userId);
        List<Long> ids = new LinkedList<>();
        int start = 0;
        while (true) {
            URIBuilder builder = buildCatalogPath(catalog, "/people/%d/%s", userId, catalog.getCreator().getPath())
                    .addParameter("start", String.valueOf(start));
            Document document = getDocument(builder, false, false);
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

    private URIBuilder buildCatalogPath(CatalogEnum catalog, String path, Object... pathArgs) {
        return super.uriBuilder(path, pathArgs).setHost(catalog.getPath() + "." + domain);
    }
}
