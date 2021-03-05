package wsg.tools.boot.pojo.entity.base;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the interface is a view of an entity,
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Target(ElementType.TYPE)
public @interface EntityView {

}
