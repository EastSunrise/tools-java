package wsg.tools.boot.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import wsg.tools.common.io.Filetype;

/**
 * Indicated that the annotated type or field represents a file stored in MinIO server, or files if
 * the target is a container like a collection or a map.
 *
 * @author Kingen
 * @since 2021/3/29
 */
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface MinioStored {

    /**
     * Returns the type of files that the annotated type or field represents.
     *
     * @return the type of the files
     */
    Filetype type();
}
