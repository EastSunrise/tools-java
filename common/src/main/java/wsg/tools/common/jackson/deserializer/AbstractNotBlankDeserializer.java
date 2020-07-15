package wsg.tools.common.jackson.deserializer;

import org.apache.commons.lang3.StringUtils;

/**
 * Base deserializer to pre-handle blank string.
 *
 * @author Kingen
 * @since 2020/7/15
 */
public abstract class AbstractNotBlankDeserializer<JavaType> extends AbstractNonNullDeserializer<JavaType, String> {

    protected AbstractNotBlankDeserializer(Class<JavaType> javaType) {
        super(javaType, String.class);
    }

    @Override
    public JavaType apply(String s) {
        if (StringUtils.isBlank(s)) {
            return null;
        }
        return parseText(s);
    }

    /**
     * Obtains an object from a string.
     *
     * @param text string
     * @return parsed object
     */
    protected abstract JavaType parseText(String text);
}
