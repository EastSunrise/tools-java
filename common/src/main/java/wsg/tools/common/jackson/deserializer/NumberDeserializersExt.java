package wsg.tools.common.jackson.deserializer;

import wsg.tools.common.util.AssertUtils;

import java.util.regex.Pattern;

/**
 * Extended deserializers for {@link Number}/
 *
 * @author Kingen
 * @since 2020/6/28
 */
public class NumberDeserializersExt {

    public static class LongDeserializer extends BaseNumberDeserializer<Long> {

        public static final LongDeserializer INSTANCE = new LongDeserializer();

        protected LongDeserializer() {
            super(Long.class);
        }

        @Override
        public Long parseNumber(String s) {
            return Long.parseLong(s);
        }
    }

    public static abstract class BaseNumberDeserializer<T extends Number> extends AbstractNonNullDeserializer<T, String> {

        private static final Pattern NUMBER_REGEX = Pattern.compile("\\d+(,\\d{3})*");

        protected BaseNumberDeserializer(Class<T> javaType) {
            super(javaType, String.class);
        }

        @Override
        public T apply(String s) {
            AssertUtils.matches(NUMBER_REGEX, s);
            return parseNumber(s.replace(",", ""));
        }

        /**
         * Obtains a number from a string
         *
         * @param s the string to parse
         * @return a number
         */
        public abstract T parseNumber(String s);
    }
}
