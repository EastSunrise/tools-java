package wsg.tools.internet.video.entity.douban.base;

import lombok.Getter;
import lombok.Setter;

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
    private Object payload;

    public boolean isSuccess() {
        return "success".equals(status);
    }
}