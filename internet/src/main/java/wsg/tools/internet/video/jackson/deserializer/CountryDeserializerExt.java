package wsg.tools.internet.video.jackson.deserializer;

import wsg.tools.common.constant.SignConstants;
import wsg.tools.common.jackson.deserializer.AbstractNotBlankDeserializer;
import wsg.tools.common.util.AssertUtils;
import wsg.tools.common.util.EnumUtilExt;
import wsg.tools.internet.video.enums.CountryEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Deserialize {@link CountryEnum, removing unnecessary info.
 *
 * @author Kingen
 * @since 2020/8/30
 */
public class CountryDeserializerExt extends AbstractNotBlankDeserializer<CountryEnum> {

    private static final Pattern TITLE_COUNTRY_REGEX = Pattern.compile("([^()]+)(\\s\\([^()]+\\))*");

    protected CountryDeserializerExt() {
        super(CountryEnum.class);
    }

    @Override
    protected CountryEnum parseText(String text) {
        if (text.startsWith(SignConstants.LEFT_PARENTHESIS)) {
            return null;
        }
        Matcher matcher = AssertUtils.matches(TITLE_COUNTRY_REGEX, text);
        return EnumUtilExt.deserializeAka(matcher.group(1), CountryEnum.class);
    }
}
