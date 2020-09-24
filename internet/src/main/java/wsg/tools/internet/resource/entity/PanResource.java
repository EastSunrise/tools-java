package wsg.tools.internet.resource.entity;

import lombok.Getter;
import wsg.tools.common.util.AssertUtils;

import java.util.Objects;
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

    private static final Pattern URI_REGEX = Pattern.compile("pan\\.baidu\\.com/s/([-0-9A-z]+)([^-0-9A-z].*)?");

    private final String path;
    private final String password;

    public PanResource(String url) {
        this(url, null);
    }

    public PanResource(String url, String password) {
        Matcher matcher = AssertUtils.find(URI_REGEX, url);
        this.path = matcher.group(1);
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PanResource that = (PanResource) o;
        return Objects.equals(path, that.path);
    }

    @Override
    public int hashCode() {
        return Objects.hash(path);
    }

    @Override
    public String getUrl() {
        return String.format("https://pan.baidu.com/s/%s", path);
    }
}
