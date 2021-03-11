package wsg.tools.internet.info.adult.wiki;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import lombok.Getter;
import wsg.tools.internet.info.adult.common.CupEnum;

/**
 * Data of BWH of a celebrity.
 *
 * @author Kingen
 * @since 2021/2/24
 */
@Getter
public final class Figure {

    private static final Pattern FIGURE_REGEX = Pattern.compile(
        "(?<b>\\d{2,3})(?<c>[A-L])?([^\\d]+)(?<w>\\d{2})\\3(?<h>\\d{2,3})[^\\d]{0,4}",
        Pattern.CASE_INSENSITIVE);
    private static final Pattern FIGURE_REGEX2 = Pattern.compile(
        "B?([.:]?)(?<b>\\d{2,3})([(（]?(?<c>[A-L])[）)]?)?([^\\d]*)"
            + "W\\1(?<w>\\d{2})\\5H\\1(?<h>\\d{2,3})[^\\d]{0,3}",
        Pattern.CASE_INSENSITIVE);
    private final int bust;
    private final int waist;
    private final int hip;
    private CupEnum cup;

    private Figure(int bust, int waist, int hip) {
        this.bust = bust;
        this.waist = waist;
        this.hip = hip;
    }

    public static Figure of(@Nonnull String text) {
        text = text.replace("cm", "");
        Matcher matcher = FIGURE_REGEX.matcher(text);
        if (matcher.matches()) {
            Figure figure = new Figure(Integer.parseInt(matcher.group("b")),
                Integer.parseInt(matcher.group("w")),
                Integer.parseInt(matcher.group("h")));
            String cup = matcher.group("c");
            if (cup != null) {
                figure.cup = Enum.valueOf(CupEnum.class, cup);
            }
            return figure;
        }
        text = text.replace(" ", "");
        Matcher matcher2 = FIGURE_REGEX2.matcher(text);
        if (matcher2.matches()) {
            Figure figure = new Figure(Integer.parseInt(matcher2.group("b")),
                Integer.parseInt(matcher2.group("w")),
                Integer.parseInt(matcher2.group("h")));
            String cup = matcher2.group("c");
            if (cup != null) {
                figure.cup = Enum.valueOf(CupEnum.class, cup);
            }
            return figure;
        }
        return null;
    }
}
