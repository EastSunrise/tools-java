package wsg.tools.internet.video.site.adult.mr;

import lombok.Getter;
import wsg.tools.common.lang.IntIdentifier;
import wsg.tools.internet.video.site.adult.mr.enums.CelebrityType;

import java.util.List;

/**
 * A celebrity with simple information.
 *
 * @author Kingen
 * @since 2021/2/26
 */
@Getter
public class SimpleCelebrity implements IntIdentifier {

    private final int id;
    private final String name;
    private final CelebrityType type;
    private String cover;
    private List<String> tags;

    SimpleCelebrity(int id, String name, CelebrityType type) {
        this.id = id;
        this.name = name;
        this.type = type;
    }

    @Override
    public int getId() {
        return id;
    }

    void setCover(String cover) {
        this.cover = cover;
    }

    void setTags(List<String> tags) {
        this.tags = tags;
    }
}