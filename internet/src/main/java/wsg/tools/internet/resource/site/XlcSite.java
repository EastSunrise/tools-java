package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.base.NotFoundException;
import wsg.tools.internet.resource.common.ResourceUtil;
import wsg.tools.internet.resource.common.VideoTypeEnum;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.BaseDetail;
import wsg.tools.internet.resource.entity.title.SimpleTitle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kingen
 * @see <a href="https://aixiaoju.com">AXJ</a>
 * @since 2020/9/9
 */
@Slf4j
public class XlcSite extends BaseResourceSite<SimpleTitle, BaseDetail> {

    private static final Pattern POSSIBLE_TITLE_REGEX =
            Pattern.compile("(\\u005B[^\\u005B\\u005D]+\\u005D)?(?<title>[^\\u005B\\u005D]+)(\\u005B[^\\u005B\\u005D]+\\u005D([^\\u005B\\u005D]+)?)?");
    private static final Pattern TITLE_HREF_REGEX = Pattern.compile("(https://www\\.(xunleicang\\.in|xlc2020\\.com))?(/vod-read-id-\\d+.html)");
    private static final int TYPE_INFO_START = "类型: ".length();

    public XlcSite() {
        super("XLC", "www.xunleicang.in", 0.1);
    }

    /**
     * Search and collect resources based on the given arguments.
     *
     * @param season current season, null for movie
     */
    public Set<AbstractResource> collect(String title, int year, @Nullable Integer season) {
        VideoTypeEnum type = season == null ? VideoTypeEnum.MOVIE : VideoTypeEnum.TV;
        Set<AbstractResource> resources = new HashSet<>();
        for (SimpleTitle item : search(title)) {
            String itemTitle = item.getTitle();
            // todo classify anime/unknown to tv/movie
            if (type != item.getType()) {
                continue;
            }
            if (!Objects.equals(year, item.getYear())) {
                continue;
            }
            if (!isPossibleTitle(title, itemTitle, year, season)) {
                continue;
            }
            BaseDetail detail = find(item);
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
            Matcher matcher = AssertUtils.matches(POSSIBLE_TITLE_REGEX, provided);
            String[] parts = matcher.group("title").split("/");
            // 2-II
            List<String> possibles = Arrays.asList(
                    target, target + "国语", target + "(未删减完整版)", target + "完整版", target + "3D",
                    target + "大电影", target + "3D版", target + "1", target + "国语D", target + "纪录片",
                    target + "国粤双语中字", target + year
            );
            return Arrays.stream(parts).anyMatch(possibles::contains);
        }

        if (season == 1) {
            List<String> possibles = Arrays.asList(
                    target, target + "第一季", target + "[" + year + "]", target + " 第一季", target + year
            );
            return Arrays.stream(provided.split("/")).anyMatch(possibles::contains);
        }

        String chineseNumeric = StringUtilsExt.chineseNumeric(season);
        String seasonStr = String.format("第%s季", chineseNumeric);
        List<String> possibles = Arrays.asList(
                target + chineseNumeric, target + seasonStr, target + " " + seasonStr
        );
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
        List<BasicNameValuePair> params = Collections.singletonList(new BasicNameValuePair("wd", keyword));
        Document document;
        try {
            document = postDocument(builder0("/vod-search"), params, true);
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        Set<SimpleTitle> titles = new HashSet<>();
        String movList = "div.movList4";
        for (Element div : document.select(movList)) {
            Element h3 = div.selectFirst(TAG_H3);
            Element a = h3.selectFirst(TAG_A);
            String typeInfo = div.selectFirst("li.playactor").nextElementSibling().text();
            int year = Integer.parseInt(a.nextElementSibling().text());
            Matcher matcher = TITLE_HREF_REGEX.matcher(a.attr(ATTR_HREF));
            if (!matcher.matches()) {
                continue;
            }
            SimpleTitle title = new SimpleTitle();
            title.setPath(matcher.group(3));
            title.setType(EnumUtilExt.deserializeAka(typeInfo.substring(TYPE_INFO_START), VideoTypeEnum.class));
            title.setTitle(a.text().strip());
            title.setYear(year == 0 ? null : year);
            titles.add(title);
        }
        return titles;
    }

    @Override
    protected final BaseDetail find(@Nonnull SimpleTitle title) {
        BaseDetail detail = new BaseDetail();
        Set<AbstractResource> resources = new HashSet<>();
        String downList = "ul.down-list";
        String item = "li.item";
        try {
            for (Element ul : getDocument(builder0(title.getPath()), true).select(downList)) {
                for (Element li : ul.select(item)) {
                    Element a = li.selectFirst(TAG_A);
                    String href = a.attr(ATTR_HREF);
                    AbstractResource resource = ResourceUtil.classifyUrl(href);
                    resource.setTitle(a.text().strip());
                    resources.add(resource);
                }
            }
        } catch (NotFoundException e) {
            throw AssertUtils.runtimeException(e);
        }
        detail.setResources(resources);
        return detail;
    }
}
