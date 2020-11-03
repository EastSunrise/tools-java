package wsg.tools.internet.resource.entity.resource.base;

import lombok.Getter;
import wsg.tools.common.constant.Constants;

import java.net.URLDecoder;
import java.util.Objects;

/**
 * Base resource with a title and a valid url.
 *
 * @author Kingen
 * @since 2020/11/1
 */
public abstract class BaseResource implements Resource {

    @Getter
    private final String title;

    protected BaseResource(String title) {
        this.title = title;
    }

    protected static String decode(String url) {
        if (url == null) {
            return null;
        }
        try {
            return URLDecoder.decode(url, Constants.UTF_8);
        } catch (IllegalArgumentException e) {
            return url;
        }
    }

    /**
     * Obtains the url of this resource.
     *
     * @return url
     */
    public abstract String getUrl();

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BaseResource that = (BaseResource) o;
        return Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
