package wsg.tools.internet.resource.entity.item.impl;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.common.lang.Identifier;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.VideoType;
import wsg.tools.internet.resource.entity.rrys.common.ChannelEnum;
import wsg.tools.internet.resource.entity.rrys.item.ResourceInfo;
import wsg.tools.internet.resource.entity.rrys.item.SeasonItem;
import wsg.tools.internet.resource.entity.rrys.jackson.PlayStatus;
import wsg.tools.internet.resource.site.impl.RrysSite;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import java.util.List;

/**
 * Items of {@link RrysSite}.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class RrysItem implements Identifier<Integer>, TypeSupplier, ImdbIdentifier {

    private final int rid;
    private final ChannelEnum channel;
    private final PlayStatus status;
    private VideoType type;
    private String imdbId;

    private ResourceInfo info;
    private List<SeasonItem> seasons;

    public RrysItem(int rid, ChannelEnum channel, PlayStatus status) {
        this.rid = rid;
        this.channel = channel;
        this.status = status;
    }

    @Override
    public Integer getId() {
        return rid;
    }

    @Override
    public VideoType getType() {
        return type;
    }

    @Override
    public String getImdbId() {
        return imdbId;
    }
}
