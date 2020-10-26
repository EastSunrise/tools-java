package wsg.tools.internet.video.entity.douban.base;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.douban.pojo.Account;

/**
 * Base class of subjects from douban.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
@Setter
public class LoginResult {

    private String status;
    private String message;
    private String description;
    private Payload payload;

    public boolean isSuccess() {
        return "success".equals(status);
    }

    @Getter
    @Setter
    public static class Payload {
        @JsonProperty("account_info")
        private Account accountInfo;

        /**
         * When it needs graph validate code.
         */
        private Long tcAppId;
        private String captchaSignatureSample;
        private String touchCapUrl;
        private String captchaId;
        private String captchaImageUrl;
    }
}