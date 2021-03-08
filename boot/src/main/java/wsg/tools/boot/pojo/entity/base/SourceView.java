package wsg.tools.boot.pojo.entity.base;

/**
 * The view of the source property of an entity.
 *
 * @author Kingen
 * @since 2021/3/8
 */
@FunctionalInterface
@EntityView
public interface SourceView {

    /**
     * Returns the source of a record.
     *
     * @return the source
     */
    Source getSource();
}
