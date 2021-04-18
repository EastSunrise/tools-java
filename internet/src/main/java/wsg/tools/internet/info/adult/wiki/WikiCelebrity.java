package wsg.tools.internet.info.adult.wiki;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A celebrity with basic information, works, and albums.
 *
 * @author Kingen
 * @see CelebrityWikiSite#findById(WikiCelebrityIndex)
 * @since 2021/2/24
 */
public class WikiCelebrity extends WikiSimpleCelebrity {

    private final BasicInfo info;
    private Set<String> works = new HashSet<>();
    private List<String> descriptions;
    private List<String> experiences;
    private List<String> groupLives;
    private List<String> awards;
    private Set<WikiAlbumIndex> albums;

    WikiCelebrity(int id, String name, WikiCelebrityType type, BasicInfo info) {
        super(id, name, type);
        this.info = info;
    }

    public BasicInfo getInfo() {
        return info;
    }

    public Set<String> getWorks() {
        return works;
    }

    void setWorks(Set<String> works) {
        this.works = Collections.unmodifiableSet(works);
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    void setDescriptions(List<String> descriptions) {
        this.descriptions = Collections.unmodifiableList(descriptions);
    }

    public List<String> getExperiences() {
        return experiences;
    }

    void setExperiences(List<String> experiences) {
        this.experiences = Collections.unmodifiableList(experiences);
    }

    public List<String> getGroupLives() {
        return groupLives;
    }

    void setGroupLives(List<String> groupLives) {
        this.groupLives = Collections.unmodifiableList(groupLives);
    }

    public List<String> getAwards() {
        return awards;
    }

    void setAwards(List<String> awards) {
        this.awards = Collections.unmodifiableList(awards);
    }

    public Set<WikiAlbumIndex> getAlbums() {
        return albums;
    }

    void setAlbums(Set<WikiAlbumIndex> albums) {
        this.albums = Collections.unmodifiableSet(albums);
    }
}
