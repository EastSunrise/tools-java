package wsg.tools.internet.resource.entity.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.common.VideoType;
import wsg.tools.internet.resource.site.BdFilmSite;

/**
 * Items of {@link BdFilmSite}.
 *
 * @author Kingen
 * @since 2020/10/27
 */
@Getter
@Setter
public class BdFilmItem extends BaseItem {

    private final VideoType type = VideoType.MOVIE;
    private Integer year;
    private Long dbId;
    private String imdbId;

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
