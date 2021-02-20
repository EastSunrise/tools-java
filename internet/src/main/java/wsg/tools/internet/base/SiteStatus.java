package wsg.tools.internet.base;

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
        INVALID;

        /**
         * Validate the status of the site based on the annotation {@link SiteStatus}.
         *
         * @throws SiteStatusException if the status is abnormal
         */
        public static void validateStatus(BaseSite site) throws SiteStatusException {
            SiteStatus annotation = site.getClass().getAnnotation(SiteStatus.class);
            if (annotation != null) {
                SiteStatus.Status status = annotation.status();
                if (!SiteStatus.Status.NORMAL.equals(status)) {
                    throw new SiteStatusException(annotation);
                }
            }
        }
    }
}
