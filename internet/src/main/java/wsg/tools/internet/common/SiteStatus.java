package wsg.tools.internet.common;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declare the status of the site.
 *
 * @author Kingen
 * @since 2020/12/25
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface SiteStatus {

    Status status() default Status.NORMAL;

    enum Status {
        /**
         * If the site is normal to access
         */
        NORMAL,
        /**
         * If the site is blocked by GFW.
         */
        BLOCKED,
        /**
         * If the site is accessible but part of resources is restricted by security verification or else.
         */
        RESTRICTED,
        /**
         * If the site is invalid.
         */
        INVALID

    }
}
