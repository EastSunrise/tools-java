package wsg.tools.internet.download.base;

import lombok.Getter;

import java.util.Objects;

/**
 * An abstract link pointing to a resource.
 *
 * @author Kingen
 * @since 2020/11/1
 */
public abstract class AbstractLink {

    @Getter
    private final String title;

    protected AbstractLink(String title) {
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
        AbstractLink that = (AbstractLink) o;
        return Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }
}
