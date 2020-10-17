package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.base.exception.NotFoundException;
import wsg.tools.internet.resource.common.ResourceUtil;
import wsg.tools.internet.resource.common.VideoTypeEnum;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.IdentifiedDetail;
import wsg.tools.internet.resource.entity.title.SimpleTitle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="http://m.y80s.com">80s</a>
 * @since 2020/9/9
 */
@Slf4j
public class Y80sSite extends BaseResourceSite<SimpleTitle, IdentifiedDetail> {

    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("//m\\.y80s\\.com(/(ju|movie|dm|trailer|mv|video|course|zy)/\\d+)");
    private static final Pattern DOUBAN_REVIEWS_REGEX = Pattern.compile("//movie.douban.com/subject/(\\d*) ?//?reviews");
    private static final String UNKNOWN_YEAR = "未知";

    public Y80sSite() {
        super("80s", SchemeEnum.HTTP, "m.y80s.com", 0.1);
    }

    /**
     * Search and collect resources based on the given arguments.
     *
     * @param season current season, null for movie
     */
    public Set<AbstractResource> collect(String title, int year, @Nullable Integer season, @Nullable Long dbId) {
        VideoTypeEnum type = season == null ? VideoTypeEnum.MOVIE : VideoTypeEnum.TV;
        Set<AbstractResource> resources = new HashSet<>();
        for (SimpleTitle item : search(title)) {
            String itemTitle = item.getTitle();
            IdentifiedDetail detail = find(item);
            Long providedId = detail.getDbId();
            if (dbId == null || providedId == null) {
                // todo classify anime to tv/movie
                if (type != item.getType()) {
                    continue;
                }
                if (!Objects.equals(year, item.getYear())) {
                    continue;
                }
                if (!isPossibleTitle(title, itemTitle, year, season)) {
                    continue;
                }
            } else if (!dbId.equals(providedId)) {
                continue;
            }
            // may include trailers, bloopers.
            log.info("Chosen title: {}", itemTitle);
            resources.addAll(detail.getResources());
        }
        return resources;
    }

    /**
     * Validate whether the title is one possible title of the given target.
     */
    private boolean isPossibleTitle(String target, String provided, int year, Integer season) {
        AssertUtils.requireNotBlank(target);
        if (StringUtils.isBlank(provided)) {
            return false;
        }

        if (season == null) {
            List<String> possibles = Arrays.asList(
                    target, target + "I", target + "1", target + year, target + "[" + year + "]",
                    target + " 加长版", target + "[DVD版]", target + " 加长版",
                    String.format("%s%02d", target, year % 100)
            );
            return Arrays.stream(provided.split("/")).anyMatch(possibles::contains);
        }

        if (season == 1) {
            List<String> possibles = Arrays.asList(
                    target, target + "第一季", target + " 第一季", target + "[第一季]", target + "[纪录片]",
                    target + year, target + "电视版", target + "[DVD]版",
                    String.format("%02d版%s", year % 100, target)
            );
            return Arrays.stream(provided.split("/")).anyMatch(possibles::contains);
        }

        String seasonStr = String.format("第%s季", StringUtilsExt.chineseNumeric(season));
        List<String> inits = Arrays.asList(
                target + season, target + seasonStr, target + " " + seasonStr, target + "[" + seasonStr + "]"
        );
        List<String> possibles = new ArrayList<>();
        inits.forEach(s -> {
            possibles.add(s);
            possibles.add(s + "[未删版]");
        });
        String[] parts = provided.split("/");
        if (provided.endsWith(seasonStr)) {
            for (int i = 0; i < parts.length - 1; i++) {
                parts[i] = parts[i] + seasonStr;
            }
        }
        return Arrays.stream(parts).anyMatch(possibles::contains);
    }

    @Override
    protected final Set<SimpleTitle> search(@Nonnull String keyword) {
        AssertUtils.requireNotBlank(keyword);
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("keyword", keyword));
        Elements as;
        try {
            as = postDocument(builder0("/search"), params, true).select("a.list-group-item");
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Set<SimpleTitle> titles = new HashSet<>();
        for (Element a : as) {
            SimpleTitle title = new SimpleTitle();
            Matcher matcher = AssertUtils.matches(TITLE_HREF_REGEX, a.attr(ATTR_HREF));
            title.setPath(matcher.group(1));
            title.setType(EnumUtilExt.deserializeAka(matcher.group(2), VideoTypeEnum.class));
            Element small = a.selectFirst(TAG_SMALL);
            title.setTitle(small.previousSibling().outerHtml().strip());
            String text = small.text();
            title.setYear((StringUtils.isBlank(text) || UNKNOWN_YEAR.equals(text)) ? null : Integer.parseInt(text));
            titles.add(title);
        }
        return titles;
    }

    @Override
    protected final IdentifiedDetail find(@Nonnull SimpleTitle title) {
        IdentifiedDetail detail = new IdentifiedDetail();
        Document document;
        try {
            document = getDocument(builder0(title.getPath()), true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        String idStr = AssertUtils.find(DOUBAN_REVIEWS_REGEX, document.selectFirst("p.col-xs-6").html()).group(1);
        if ("".equals(idStr)) {
            detail.setDbId(null);
        } else {
            long id = Long.parseLong(idStr);
            detail.setDbId(id <= 1000000 ? null : id);
        }
        Set<AbstractResource> resources = new HashSet<>();
        for (Element tr : document.select(TAG_TR)) {
            Element a = tr.selectFirst(TAG_A);
            AbstractResource resource = ResourceUtil.classifyUrl(a.attr(ATTR_HREF));
            resource.setTitle(a.text().strip());
            resources.add(resource);
        }
        detail.setResources(resources);
        return detail;
    }
}
