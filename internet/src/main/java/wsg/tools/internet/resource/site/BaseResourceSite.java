package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import wsg.tools.common.util.StringUtilsExt;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.enums.SchemeEnum;
import wsg.tools.internet.resource.entity.CollectResult;
import wsg.tools.internet.resource.entity.title.BaseDetail;
import wsg.tools.internet.resource.entity.title.BaseItem;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Base class of sites of resources of video.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Slf4j
public abstract class BaseResourceSite<T extends BaseItem, D extends BaseDetail> extends BaseSite {

    protected BaseResourceSite(String name, String host) {
        super(name, host);
    }

    protected BaseResourceSite(String name, String domain, double postPermitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, postPermitsPerSecond);
    }

    protected BaseResourceSite(String name, SchemeEnum scheme, String domain, double postPermitsPerSecond) {
        super(name, scheme, domain, 1, postPermitsPerSecond);
    }

    /**
     * Collect all available resources by the given keyword.
     * <p>
     * The resources are not filtered.
     */
    public CollectResult<T> collect(String keyword) {
        CollectResult<T> result = new CollectResult<>();
        for (T title : search(keyword)) {
            result.include(find(title).getResources());
        }
        return result;
    }

    protected final boolean isPossibleSeason1(String target, String provided, int year, Integer season) {
        if (season == null) {
            List<String> possibles = Arrays.asList(
                    target,
                    target + "I", target + "1",
                    target + year, target + "[" + year + "]", String.format("%s%02d", target, year % 100),
                    target + " 加长版", target + "[DVD版]", target + "(未删减完整版)", target + "完整版",
                    target + "3D", target + "大电影", target + "3D版", target + "纪录片",
                    target + "国粤双语中字", target + "高清", target + "加长"
            );
            return Arrays.stream(provided.split("/")).anyMatch(possibles::contains);
        }

        if (season == 1) {
            List<String> possibles = Arrays.asList(
                    target,
                    target + "第一季", target + " 第一季", target + "[第一季]",
                    target + "第一部", target + " 第一部", target + "[第一部]",
                    target + year, target + "[" + year + "]", String.format("%02d版%s", year % 100, target),
                    target + "[纪录片]", target + "电视版", target + "[DVD]版", target + "D", target + "(电视剧)"
            );
            return Arrays.stream(provided.split("/")).anyMatch(possibles::contains);
        }

        String chineseNumeric = StringUtilsExt.chineseNumeric(season);
        String seasonStr = String.format("第%s季", chineseNumeric);
        String seasonStr2 = String.format("第%s部", chineseNumeric);
        List<String> inits = Arrays.asList(
                target + season, target + chineseNumeric,
                target + seasonStr, target + " " + seasonStr, target + "[" + seasonStr + "]",
                target + seasonStr2, target + " " + seasonStr2, target + "[" + seasonStr2 + "]"
        );
        List<String> possibles = new ArrayList<>();
        inits.forEach(s -> {
            possibles.add(s);
            possibles.add(s + "[未删版]");
            possibles.add(s + "[未删]");
        });
        String[] parts = provided.split("/");
        if (provided.endsWith(seasonStr)) {
            for (int i = 0; i < parts.length - 1; i++) {
                parts[i] = parts[i] + seasonStr;
            }
        }
        return Arrays.stream(parts).anyMatch(possibles::contains);
    }

    /**
     * Search titles for the given keyword.
     *
     * @param keyword keyword to search
     * @return list of returned items
     */
    protected abstract Set<T> search(@Nonnull String keyword);

    /**
     * Obtains details of the given item.
     *
     * @param item given item of the page of the resources
     * @return resource
     */
    protected abstract D find(@Nonnull T item);
}
