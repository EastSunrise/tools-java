package wsg.tools.internet.resource.entity.rrys.item;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.common.ChannelEnum;
import wsg.tools.internet.resource.entity.rrys.common.LevelEnum;
import wsg.tools.internet.resource.entity.rrys.common.ResourceTypeEnum;
import wsg.tools.internet.resource.entity.rrys.jackson.PlayStatus;
import wsg.tools.internet.video.jackson.annotation.JoinedValue;

import java.time.Year;
import java.util.List;

/**
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class CateRankItem extends ResourceItem {

    private ChannelEnum channel;
    private ResourceTypeEnum type;
    private PlayStatus playStatus;
    private LevelEnum level;
    @JoinedValue(separator = "/")
    private List<String> category;

    private Double score;
    private Integer rank;
    private Integer views;
    private Integer favorites;
    private Integer tplCount;
    private Year publishYear;

    private String poster;
    private String posterB;
    private String school;
}
