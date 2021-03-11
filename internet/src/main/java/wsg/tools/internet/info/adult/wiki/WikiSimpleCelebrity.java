package wsg.tools.internet.info.adult.wiki;

import java.util.Collections;
import java.util.List;
import lombok.Getter;

/**
 * A celebrity with simple information.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class WikiSimpleCelebrity extends WikiCelebrityIndex {

    private final WikiCelebrityType type;
    private String cover;
    private List<String> tags;

    WikiSimpleCelebrity(int id, String name, WikiCelebrityType type) {
        super(id, name);
        this.type = type;
    }

    void setCover(String cover) {
        this.cover = cover;
    }

    void setTags(List<String> tags) {
        this.tags = Collections.unmodifiableList(tags);
    }
}
