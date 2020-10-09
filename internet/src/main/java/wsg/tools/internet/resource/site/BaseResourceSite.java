package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.SchemeEnum;
import wsg.tools.internet.resource.entity.resource.AbstractResource;
import wsg.tools.internet.resource.entity.title.BaseDetail;
import wsg.tools.internet.resource.entity.title.BaseTitle;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class of sites of resources of video.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Slf4j
public abstract class BaseResourceSite<T extends BaseTitle, D extends BaseDetail> extends BaseSite {

    private static final Pattern POSSIBLE_TITLE_REGEX = Pattern.compile("(\\[[^\\[\\]]+])?([^\\[\\]]+)(\\[[^\\[\\]]+][^\\[\\]]*)?");

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
    public Set<AbstractResource> collect(String keyword) {
        Set<AbstractResource> resources = new HashSet<>();
        for (T title : search(keyword)) {
            resources.addAll(find(title).getResources());
        }
        return resources;
    }

    /**
     * Search titles for the given keyword.
     *
     * @param keyword keyword to search
     * @return list of returned items
     */
    protected abstract Set<T> search(@Nonnull String keyword);

    /**
     * Obtains details of the given title.
     *
     * @param title given title of the page of the resources
     * @return resource
     */
    protected abstract D find(@Nonnull T title);

    protected final <V> boolean validate(String title, V provided, V target) {
        boolean equals = Objects.equals(provided, target);
        if (!equals) {
            log.error("Excluded title: {}, required: {}, provided: {}.", title, target, provided);
        }
        return equals;
    }

    /**
     * Validate whether the title is one possible title of the given target.
     */
    protected final boolean notPossibleTitle(String provided, final String target, int year) {
        if (StringUtils.isBlank(provided)) {
            return true;
        }
        HashSet<String> possibles = new HashSet<>(Arrays.asList(
                target, target + "I", target + "1", target + year, target + "国语",
                target + "DVD版", target + "完整版", target + "(未删减完整版)", target + "3D",
                target + " 加长版"
        ));
        for (String part : StringUtils.split(provided, SignEnum.SLASH.getC())) {
            Matcher matcher = AssertUtils.matches(POSSIBLE_TITLE_REGEX, part);
            if (possibles.contains(matcher.group(2))) {
                return false;
            }
        }
        log.error("Excluded title: {}, target: {}.", provided, target);
        return true;
    }
}
