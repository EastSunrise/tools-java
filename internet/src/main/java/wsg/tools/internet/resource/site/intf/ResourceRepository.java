package wsg.tools.internet.resource.site.intf;

import wsg.tools.internet.base.exception.NotFoundException;

import java.util.List;

/**
 * Interface for generic CRUD operations on a resource site for its items.
 *
 * @author Kingen
 * @since 2021/1/12
 */
public interface ResourceRepository<T, ID> {

    /**
     * Obtains all available items on the site.
     *
     * @return all items
     */
    List<T> findAll();

    /**
     * Obtains details of the item.
     *
     * @param id identifier of the item
     * @return item
     * @throws NotFoundException if not found
     */
    T findById(ID id) throws NotFoundException;
}
