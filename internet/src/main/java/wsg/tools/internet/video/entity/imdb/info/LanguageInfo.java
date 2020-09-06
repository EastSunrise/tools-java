package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.entity.imdb.base.BaseImdbInfo;
import wsg.tools.internet.video.enums.LanguageEnum;

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
