package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Quotes from the title.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class QuoteInfo {

    private List<Role> roles;
    private List<String> quotes;

    @Getter
    @Setter
    public static class Role {
        private String actor;
        private String role;
    }
}
