package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.SchemeEnum;
import wsg.tools.internet.resource.entity.AbstractResource;
import wsg.tools.internet.resource.entity.BaseDetail;
import wsg.tools.internet.resource.entity.BaseTitle;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
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
public abstract class AbstractVideoResourceSite<T extends BaseTitle, D extends BaseDetail> extends BaseSite {

    private static final Pattern POSSIBLE_TITLE_REGEX = Pattern.compile("(\\[[^\\[\\]]+])?([^\\[\\]]+)(\\[[^\\[\\]]+][^\\[\\]]*)?");

    protected AbstractVideoResourceSite(String name, String host) {
        super(name, host);
    }

    protected AbstractVideoResourceSite(String name, String domain, double postPermitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, postPermitsPerSecond);
    }

    protected AbstractVideoResourceSite(String name, SchemeEnum scheme, String domain, double postPermitsPerSecond) {
        super(name, scheme, domain, 1, postPermitsPerSecond);
    }

    /**
     * Collect all available resources by the given keyword.
     * <p>
     * The resources are not filtered.
     */
    public Set<AbstractResource> collect(String keyword) throws IOException {
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
     * @throws IOException request exception
     */
    protected abstract Set<T> search(@Nonnull String keyword) throws IOException;

    /**
     * Obtains details of the given title.
     *
     * @param title given title of the page of the resources
     * @return resource
     * @throws IOException request exception
     */
    protected abstract D find(@Nonnull T title) throws IOException;

    /**
     * Validate whether the title is one possible title of the given target.
     */
    protected boolean notPossibleTitles(final String srcTitle, int year, String itemTitle) {
        if (StringUtils.isBlank(itemTitle)) {
            return true;
        }
        HashSet<String> possibles = new HashSet<>(Arrays.asList(
                srcTitle, srcTitle + "I", srcTitle + "1", srcTitle + year, srcTitle + "国语",
                srcTitle + "DVD版", srcTitle + "完整版", srcTitle + "(未删减完整版)", srcTitle + "3D",
                srcTitle + " 加长版"
        ));
        for (String part : StringUtils.split(itemTitle, SignEnum.SLASH.getC())) {
            Matcher matcher = AssertUtils.matches(POSSIBLE_TITLE_REGEX, part);
            if (possibles.contains(matcher.group(2))) {
                return false;
            }
        }
        return true;
    }
}
