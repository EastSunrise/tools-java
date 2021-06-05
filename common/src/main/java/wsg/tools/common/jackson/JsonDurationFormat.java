package wsg.tools.common.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicated the annotated property is a {@link java.time.Duration} and the format.
 *
 * @author Kingen
 * @since 2020/9/2
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = FormattedDurationDeserializer.class)
public @interface JsonDurationFormat {

    /**
     * the format of the annotated property
     */
    Format format() default Format.DEFAULT;

    enum Format {

        /**
         * the type int/long
         */
        LONG,
        /**
         * the type float/double
         */
        DOUBLE,
        /**
         * the format like '12:44:66'
         *
         * @see wsg.tools.common.time.TimeUtils#parseDuration(String)
         */
        DURATION,
        /**
         * the default format
         *
         * @see java.time.Duration#parse(CharSequence)
         */
        DEFAULT
    }
}
