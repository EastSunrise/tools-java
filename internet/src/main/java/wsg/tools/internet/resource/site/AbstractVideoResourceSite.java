package wsg.tools.internet.resource.site;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import wsg.tools.common.constant.SignEnum;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.SchemeEnum;
import wsg.tools.internet.resource.entity.AbstractResource;
import wsg.tools.internet.resource.entity.SimpleTitle;
import wsg.tools.internet.resource.entity.TitleDetail;
import wsg.tools.internet.video.site.DoubanSite;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Base class of sites of resources of video.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@Slf4j
public abstract class AbstractVideoResourceSite<T extends TitleDetail> extends BaseSite<Void> {

    private static final Pattern POSSIBLE_TITLE_REGEX = Pattern.compile("(\\[[^\\[\\]]+])?([^\\[\\]]+)(\\[[^\\[\\]]+][^\\[\\]]*)?");

    protected AbstractVideoResourceSite(String name, String domain, double postPermitsPerSecond) {
        this(name, SchemeEnum.HTTPS, domain, postPermitsPerSecond);
    }

    protected AbstractVideoResourceSite(String name, SchemeEnum scheme, String domain, double postPermitsPerSecond) {
        super(name, scheme, domain, 1, postPermitsPerSecond);
    }

    /**
     * Search and collect resources based on the given arguments.
     *
     * @param title title of the subject
     * @param year  year of the subject
     * @param dbId  corresponding id on {@link DoubanSite}, may null
     * @return list of resources
     * @throws IOException exception when requesting
     */
    public abstract List<AbstractResource> collectMovie(@Nonnull String title, @Nonnull Year year, @Nullable Long dbId) throws IOException;

    /**
     * Collect all available resources by the given keyword.
     * <p>
     * The resources are not filtered.
     */
    public List<AbstractResource> collect(String keyword) throws IOException {
        List<AbstractResource> resources = new ArrayList<>();
        for (SimpleTitle title : search(keyword)) {
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
    public abstract List<SimpleTitle> search(@Nonnull String keyword) throws IOException;

    /**
     * Obtains details of the given title.
     *
     * @param title given title of the page of the resources
     * @return resource
     * @throws IOException request exception
     */
    public abstract T find(@Nonnull SimpleTitle title) throws IOException;

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
