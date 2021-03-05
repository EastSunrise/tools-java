package wsg.tools.boot.pojo.entity.base;

/**
 * Only query id of an entity.
 *
 * @author Kingen
 * @since 2020/9/9
 */
@EntityView
@FunctionalInterface
public interface IdView<ID> {

    /**
     * Id of database.
     *
     * @return id
     */
    ID getId();
}
