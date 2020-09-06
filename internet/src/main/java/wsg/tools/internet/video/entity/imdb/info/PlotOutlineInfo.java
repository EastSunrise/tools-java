package wsg.tools.internet.video.entity.imdb.info;

import lombok.Getter;
import lombok.Setter;
import wsg.tools.internet.video.enums.LanguageEnum;

/**
 * Plot outlines: no longer than 3 lines, or 239 characters.
 *
 * @author Kingen
 * @see SummaryInfo for longer plots
 * @since 2020/9/4
 */
@Getter
@Setter
public class PlotOutlineInfo {

    private LanguageEnum language;
    private String outline;
    private String by;
}
