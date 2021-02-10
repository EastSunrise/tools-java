package wsg.tools.internet.resource.base;

import lombok.Getter;

import java.util.Objects;

/**
 * Base resource with a title and a url.
 *
 * @author Kingen
 * @since 2020/11/1
 */
public abstract class AbstractResource {

    @Getter
    private final String title;

    protected AbstractResource(String title) {
        this.title = title;
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
        AbstractResource that = (AbstractResource) o;
        return Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
