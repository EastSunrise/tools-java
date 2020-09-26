package wsg.tools.boot.pojo.entity;

/**
 * Only query id of an entity.
 *
 * @author Kingen
 * @since 2020/9/9
 */
public interface IdSupplier {

    /**
     * Id of database.
     *
     * @return id
     */
    Long getId();
}
