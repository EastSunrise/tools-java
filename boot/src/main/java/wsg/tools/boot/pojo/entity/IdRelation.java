package wsg.tools.boot.pojo.entity;

/**
 * Relation between ids of database, those from Douban, and those from IMDb.
 *
 * @author Kingen
 * @since 2020/9/9
 */
public interface IdRelation {

    /**
     * Id of database.
     *
     * @return id
     */
    Long getId();

    /**
     * Id of Douban
     *
     * @return id
     */
    Long getDbId();

    /**
     * Id of IMDb
     *
     * @return id
     */
    String getImdbId();
}
