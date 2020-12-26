package wsg.tools.internet.resource.entity.rrys.info;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.resource.entity.rrys.common.CommonGroupEnum;

/**
 * @author Kingen
 * @since 2020/12/24
 */
@Getter
@Setter
@JsonIgnoreProperties("common_group_name")
public class HotUser {

    private Long uid;
    private String nickname;
    private Integer posts;
    private String avatarS;

    @JsonProperty("common_group_id")
    private CommonGroupEnum commonGroup;
    private String groupName;
}
