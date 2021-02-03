package wsg.tools.internet.resource.site.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.EnumUtils;
import org.apache.http.client.utils.URIBuilder;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.jackson.deserializer.EnumDeserializers;
import wsg.tools.common.jackson.deserializer.TimestampDeserializer;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.util.regex.RegexUtils;
import wsg.tools.internet.base.enums.ContentTypeEnum;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.base.exception.UnexpectedContentException;
import wsg.tools.internet.resource.entity.item.impl.RrysItem;
import wsg.tools.internet.resource.entity.rrys.common.*;
import wsg.tools.internet.resource.entity.rrys.info.IndexInfo;
import wsg.tools.internet.resource.entity.rrys.item.ResourceInfo;
import wsg.tools.internet.resource.entity.rrys.item.SeasonItem;
import wsg.tools.internet.resource.entity.rrys.jackson.PlayStatus;
import wsg.tools.internet.resource.entity.rrys.jackson.PlayStatusDeserializer;
import wsg.tools.internet.resource.entity.rrys.jackson.RrysProblemHandler;
import wsg.tools.internet.video.enums.RegionEnum;

import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;


/**
 * @author Kingen
 * @see <a href="https://www.yyets.com/">RRYS</a>
 * @since 2020/12/23
 */
@Slf4j
public class RrysSite extends AbstractResourceSite<RrysItem, Integer> {

    private static final Pattern RESOURCE_HREF_REGEX = Pattern.compile("/resource/(?<rid>\\d+)");
    private static final Pattern RESOURCE_SRC_REGEX = Pattern.compile("/resource/index_json/rid/(?<rid>\\d+)/channel/(?<c>tv|movie|openclass)");
    private static final Pattern INDEX_INFO_REGEX = Pattern.compile("var index_info=(?<info>\\{.*})");
    private static final Pattern RESOURCE_URL_REGEX = Pattern.compile("http://got002\\.com/resource\\.html\\?code=(?<c>[0-9A-Za-z]+)");
    private static final Pattern IMDB_URL_REGEX = Pattern.compile("http://www\\.imdb\\.com/title/(?<id>tt\\d+)/");

    public RrysSite() {
        super("RRYS", SchemeEnum.HTTP, "rrys2020.com", 10D);
    }

    @Override
    public List<RrysItem> findAll() {
        List<RrysItem> items = new ArrayList<>();
        IntStream.rangeClosed(1, getMaxPage()).forEach(page -> {
            Document document;
            try {
                document = getDocument(builder0("/resourcelist")
                        .setParameter("page", String.valueOf(page))
                        .setParameter("sort", "pubdate"), true);
            } catch (NotFoundException e) {
                throw AssertUtils.runtimeException(e);
            }
            Element showList = document.selectFirst(".resource-showlist");
            for (Element li : showList.select(TAG_LI)) {
                String href = li.selectFirst(TAG_A).attr(ATTR_HREF);
                try {
                    items.add(findById(Integer.parseInt(RegexUtils.matchesOrElseThrow(RESOURCE_HREF_REGEX, href).group("rid"))));
                } catch (NotFoundException ignored) {
                }
            }
        });
        return items;
    }

    /**
     * @see <a href="http://www.rrys2020.com/resourcelist">Resource List</a>
     */
    private int getMaxPage() {
        Document document;
        try {
            document = getDocument(builder0("/resourcelist"), false);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Elements as = document.selectFirst(".pages").select(TAG_A);
        return Integer.parseInt(as.get(as.size() - 2).text().substring(3));
    }

    @Override
    public RrysItem findById(Integer id) throws NotFoundException {
        if (id < 1) {
            throw new NotFoundException("Not a valid id.");
        }
        URIBuilder builder = builder0("/resource/%d", id);
        Document document = getDocument(builder, true);
        Elements scripts = document.body().select(TAG_SCRIPT);
        for (Element script : scripts) {
            String src = script.attr(ATTR_SRC);
            Matcher matcher = RESOURCE_SRC_REGEX.matcher(src);
            if (matcher.matches()) {
                return extractInfo(matcher, document);
            }
        }
        throw new UnexpectedContentException("Can't find index json.");
    }

    private RrysItem extractInfo(Matcher matcher, Document document) throws NotFoundException {
        String content = getContent(builder0(matcher.group()), ContentTypeEnum.JS, true);
        String str = RegexUtils.matchesOrElseThrow(INDEX_INFO_REGEX, content).group("info");
        IndexInfo indexInfo;
        try {
            indexInfo = this.mapper.readValue(str, IndexInfo.class);
        } catch (JsonProcessingException e) {
            throw AssertUtils.runtimeException(e);
        }
        ChannelEnum channel = EnumUtils.getEnumIgnoreCase(ChannelEnum.class, matcher.group("c"));
        RrysItem rrysItem = new RrysItem(Integer.parseInt(matcher.group("rid")), channel, indexInfo.getPlayStatus());

        Element div = document.selectFirst(".fl-info");
        Map<String, Element> lis = new HashMap<>(16);
        for (Element li : div.select(TAG_LI)) {
            lis.put(li.firstElementSibling().text(), li);
        }
        Element li = lis.get("IMDB：");
        if (li != null) {
            String href = li.selectFirst(TAG_A).attr(ATTR_HREF);
            rrysItem.setImdbId(RegexUtils.matchesOrElseThrow(IMDB_URL_REGEX, href).group("id"));
        }

        Element button = Jsoup.parse(indexInfo.getResourceContent()).selectFirst(TAG_H3).selectFirst(TAG_A);
        if (button == null) {
            return rrysItem;
        }
        Matcher gotMatcher = RESOURCE_URL_REGEX.matcher(button.attr(ATTR_HREF));
        if (!gotMatcher.matches()) {
            return rrysItem;
        }

        Data data;
        try {
            String code = gotMatcher.group("c");
            data = getObject(new URIBuilder("http://got002.com/api/v1/static/resource/detail?code=" + code), Response.class).data;
        } catch (NotFoundException | URISyntaxException e) {
            throw AssertUtils.runtimeException(e);
        }
        rrysItem.setInfo(data.info);
        rrysItem.setSeasons(data.seasons);
        return rrysItem;
    }

    @Override
    protected ObjectMapper objectMapper() {
        return super.objectMapper()
                .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                .registerModule(new SimpleModule()
                        .addDeserializer(RegionEnum.class, EnumDeserializers.getAkaDeserializer(String.class, RegionEnum.class))
                        .addDeserializer(ResourceTypeEnum.class, EnumDeserializers.getTitleDeserializer(ResourceTypeEnum.class))
                        .addDeserializer(FormatEnum.class, EnumDeserializers.getTextDeserializer(FormatEnum.class))
                        .addDeserializer(FileWayEnum.class, EnumDeserializers.getCodeDeserializer(Integer.class, FileWayEnum.class))
                        .addDeserializer(CommonGroupEnum.class, EnumDeserializers.getCodeDeserializer(Integer.class, CommonGroupEnum.class))
                        .addDeserializer(PlayStatus.class, PlayStatusDeserializer.INSTANCE)
                ).registerModule(new JavaTimeModule()
                        .addDeserializer(LocalDateTime.class, TimestampDeserializer.getInstance())
                        .addDeserializer(DayOfWeek.class, EnumDeserializers.getDeserializer(DayOfWeek.class, String.class,
                                ((dayOfWeek, s) -> dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.CHINA).equals(s))))
                ).addHandler(new RrysProblemHandler());
    }

    @Getter
    @Setter
    private static class Response {
        private Data data;
        private int status;
        private String info;
    }

    @Getter
    @Setter
    private static class Data {
        private ResourceInfo info;
        private List<SeasonItem> seasons;
    }
}
