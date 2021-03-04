package wsg.tools.internet.movie.common.jackson;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

/**
 * Customized property naming strategies when converting json.
 *
 * @author Kingen
 * @since 2020/8/31
 */
public final class PropertyNamingStrategies {
    /**
     * A {@link PropertyNamingStrategy} that translates typical camel case Java
     * property names to upper case JSON element names, separated by space, such as:
     * <li>&quot;userName&quot; is translated to &quot;User Name&quot;</li>
     */
    public static class UpperSpaceStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase {

        private static final char SEPARATOR = ' ';

        @Override
        public String translate(String input) {
            if (input == null) {
                return null;
            }
            final int length = input.length();
            if (length == 0) {
                return input;
            }

            final StringBuilder result = new StringBuilder(length + (length >> 1));
            int upperCount = 0;
            for (int i = 0; i < length; ++i) {
                char ch = input.charAt(i);
                char lc = Character.toLowerCase(ch);
                if (lc == ch) {
                    if (upperCount > 1) {
                        result.insert(result.length() - 1, SEPARATOR);
                    }
                    upperCount = 0;
                    if (i == 0) {
                        result.append(Character.toUpperCase(ch));
                    } else {
                        result.append(ch);
                    }
                } else {
                    if ((upperCount == 0) && (i > 0)) {
                        result.append(SEPARATOR);
                    }
                    ++upperCount;
                    result.append(ch);
                }
            }
            return result.toString();
        }
    }
}
