package wsg.tools.internet.info.adult.mr;

import lombok.Getter;

import java.util.List;
import java.util.Set;

/**
 * A celebrity with basic information, works, and albums.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public class Celebrity extends SimpleCelebrity {

    private final BasicInfo info;
    private List<SimpleAdultEntry> works;
    private List<String> descriptions;
    private List<String> experiences;
    private List<String> groupLives;
    private List<String> awards;
    private Set<SimpleAlbum> albums;

    Celebrity(int id, String name, CelebrityType type, BasicInfo info) {
        super(id, name, type);
        this.info = info;
    }

    void setWorks(List<SimpleAdultEntry> works) {
        this.works = works;
    }

    void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    void setExperiences(List<String> experiences) {
        this.experiences = experiences;
    }

    void setGroupLives(List<String> groupLives) {
        this.groupLives = groupLives;
    }

    void setAwards(List<String> awards) {
        this.awards = awards;
    }

    void setAlbums(Set<SimpleAlbum> albums) {
        this.albums = albums;
    }
}
