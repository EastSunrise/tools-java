package wsg.tools.internet.resource.entity;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.util.AssertUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resources of baidu disk.
 *
 * @author Kingen
 * @since 2020/9/18
 */
@Getter
public class PanResource extends AbstractResource {

    private static final Pattern URI_REGEX = Pattern.compile("https?://pan\\.baidu\\.com/s/([-0-9A-z]+)([^-0-9A-z].*)?");

    private final String path;
    @Setter
    private String password;

    public PanResource(String url) {
        Matcher matcher = AssertUtils.matches(URI_REGEX, url);
        this.path = matcher.group(1);
    }

    @Override
    public String getUrl() {
        return String.format("https://pan.baidu.com/s/%s", path);
    }
}
