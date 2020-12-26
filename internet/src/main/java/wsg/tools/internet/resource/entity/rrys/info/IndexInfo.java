package wsg.tools.internet.resource.entity.rrys.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.item.CateRankItem;
import wsg.tools.internet.resource.entity.rrys.item.SimilarItem;
import wsg.tools.internet.resource.entity.rrys.jackson.PlayStatus;

import java.util.List;

/**
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
public class IndexInfo {

    private List<CateRankItem> cateRanks;
    private List<HotUser> hotUser;
    private List<SimilarItem> similar;
    private Integer followed;
    private Object manage;
    private FollowStatus followStatus;
    private List<Prevue> prevue;
    private Watch watch;
    private PlayStatus playStatus;
    private Integer updatecache;
    private Integer views;
    private String resourceContent;
}
