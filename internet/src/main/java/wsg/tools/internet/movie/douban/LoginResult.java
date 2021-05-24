package wsg.tools.internet.movie.douban;

import com.fasterxml.jackson.annotation.JsonProperty;
import wsg.tools.internet.movie.douban.api.pojo.Account;

/**
 * Base class of subjects from douban.
 *
 * @author Kingen
 * @since 2020/8/31
 */
class LoginResult {

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

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public String getDescription() {
        return description;
    }

    public Payload getPayload() {
        return payload;
    }

    static class Payload {

        @JsonProperty("account_info")
        private Account account;

        /**
         * When graph validate code is required.
         */
        @JsonProperty("tc_app_id")
        private Long tcAppId;
        @JsonProperty("captcha_signature_sample")
        private String captchaSignatureSample;
        @JsonProperty("touch_cap_url")
        private String touchCapUrl;
        @JsonProperty("captcha_id")
        private String captchaId;
        @JsonProperty("captcha_image_url")
        private String captchaImageUrl;

        public Account getAccount() {
            return account;
        }

        public Long getTcAppId() {
            return tcAppId;
        }

        public String getCaptchaSignatureSample() {
            return captchaSignatureSample;
        }

        public String getTouchCapUrl() {
            return touchCapUrl;
        }

        public String getCaptchaId() {
            return captchaId;
        }

        public String getCaptchaImageUrl() {
            return captchaImageUrl;
        }
    }
}