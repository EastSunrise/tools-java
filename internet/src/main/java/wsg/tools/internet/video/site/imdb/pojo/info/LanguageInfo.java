package wsg.tools.internet.video.site.imdb.pojo.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.LanguageEnum;
import wsg.tools.internet.video.site.imdb.pojo.base.BaseImdbInfo;

/**
 * Info of languages.
 * <p>
 * Attribute: such as (a few words), (a few lines), (song), etc.
 *
 * @author Kingen
 * @since 2020/9/4
 */
@Getter
@Setter
public class LanguageInfo extends BaseImdbInfo {

    private LanguageEnum language;
}
