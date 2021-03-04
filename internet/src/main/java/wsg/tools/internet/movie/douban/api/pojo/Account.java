package wsg.tools.internet.movie.douban.api.pojo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

/**
 * Accounts of douban.
 *
 * @author Kingen
 * @since 2020/9/19
 */
@Getter
public class Account extends User {

    private Long phone;
    @JsonProperty("weixin_binded")
    private Boolean weixinBinded;
}