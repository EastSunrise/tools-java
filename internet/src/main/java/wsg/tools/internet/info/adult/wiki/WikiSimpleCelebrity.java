package wsg.tools.internet.info.adult.wiki;

import java.net.URL;
import wsg.tools.internet.base.view.CoverSupplier;
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
    private WikiCelebrityType type;
    private URL cover;
    private String[] tags;

    WikiSimpleCelebrity(int id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public WikiCelebrityType getType() {
        return type;
    }

    @Override
    public URL getCover() {
        return cover;
    }

    void setCover(URL cover) {
        this.cover = cover;
    }

    @Override
    public String[] getTags() {
        return tags;
    }

    void setTags(String[] tags) {
        this.tags = tags;
    }
}
