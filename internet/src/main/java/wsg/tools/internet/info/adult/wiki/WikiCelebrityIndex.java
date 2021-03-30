package wsg.tools.internet.info.adult.wiki;

import lombok.Getter;
import wsg.tools.internet.base.IntIdentifier;

/**
 * An index pointing to a {@link WikiCelebrity} in the {@link CelebrityWikiSite}.
 *
 * @author Kingen
 * @since 2021/3/11
 */
@Getter
public class WikiCelebrityIndex implements IntIdentifier {

    private final int id;
    private final String name;

    WikiCelebrityIndex(int id, String name) {
        this.id = id;
        this.name = name;
    }
}
