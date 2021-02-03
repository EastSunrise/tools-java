package wsg.tools.internet.resource.entity.resource.base;

import lombok.Getter;

import java.util.Objects;

/**
 * Base resource with a title and a url.
 *
 * @author Kingen
 * @since 2020/11/1
 */
public abstract class Resource {

    @Getter
    private final String title;

    protected Resource(String title) {
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
        Resource that = (Resource) o;
        return Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
