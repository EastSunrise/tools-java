package wsg.tools.internet.video.entity.douban.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * Accounts of douban.
 *
 * @author Kingen
 * @since 2020/9/19
 */
@Setter
@Getter
public class Account extends User {

    private Long phone;
    @JsonProperty("weixin_binded")
    private Boolean weixinBinded;
}