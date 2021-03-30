package wsg.tools.internet.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates the annotated type is a site. This annotation should be used on a concrete
 * implementation of sites.
 *
 * @author Kingen
 * @since 2020/12/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ConcreteSite {

    /**
     * Marks the status of annotated site, normal by default
     */
    SiteStatus status() default SiteStatus.NORMAL;
}
