package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import wsg.tools.internet.movie.douban.api.pojo.Account;

/**
 * Base class of subjects from douban.
 *
 * @author Kingen
 * @since 2020/8/31
 */
@Getter
public class LoginResult {

    @JsonProperty("status")
    private String status;
    @JsonProperty("message")
    private String message;
    @JsonProperty("description")
    private String description;
    @JsonProperty("payload")
    private Payload payload;

    LoginResult() {
    }

    public boolean isSuccess() {
        return "success".equals(status);
    }

    @Getter
    public static class Payload {
        @JsonProperty("account_info")
        private Account account;

        /**
         * When graph validate code is required.
         */
        private Long tcAppId;
        private String captchaSignatureSample;
        private String touchCapUrl;
        private String captchaId;
        private String captchaImageUrl;
    }
}