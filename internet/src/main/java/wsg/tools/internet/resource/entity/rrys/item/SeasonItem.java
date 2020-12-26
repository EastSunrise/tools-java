package wsg.tools.internet.resource.entity.rrys.item;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.common.FormatEnum;

import java.util.HashMap;
import java.util.List;

/**
 * An item of a season.
 *
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class SeasonItem {

    @JsonProperty("season_num")
    private Integer currentSeason;
    private String seasonCn;
    private List<FormatEnum> formats;
    private HashMap<FormatEnum, List<EpisodeItem>> items;
}
