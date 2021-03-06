package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import wsg.tools.internet.common.CoverSupplier;
import wsg.tools.internet.info.adult.view.Tagged;

/**
 * A celebrity with simple information.
 *
 * @author Kingen
 * @see WikiAdultEntry#getCelebrity()
 * @see WikiCelebrity
 * @since 2021/2/26
 */
public class WikiSimpleCelebrity implements WikiCelebrityIndex, CoverSupplier, Tagged {

    private final int id;
    private final String name;
    private final WikiCelebrityType type;
    private URL cover;
    private Set<String> tags = new HashSet<>(0);

    WikiSimpleCelebrity(int id, String name, WikiCelebrityType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public WikiCelebrityType getSubtype() {
        return type;
    }

    @Override
    public URL getCoverURL() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = cover;
    }

    @Nonnull
    @Override
    public Set<String> getTags() {
        return tags;
    }

    void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
