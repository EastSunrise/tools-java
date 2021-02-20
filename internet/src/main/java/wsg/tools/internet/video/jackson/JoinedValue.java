package wsg.tools.internet.video.jackson;

import com.fasterxml.jackson.annotation.JacksonAnnotationsInside;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation indicated the annotated property is deserialized from a joined string.
 *
 * @author Kingen
 * @since 2020/9/2
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotationsInside
@JsonDeserialize(using = JoinedValueDeserializer.class)
public @interface JoinedValue {

    /**
     * separator which deserialized string is joined with.
     */
    String separator() default JoinedValueDeserializer.DEFAULT_SEPARATOR;
}
