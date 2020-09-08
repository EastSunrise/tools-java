package wsg.tools.boot.pojo.base;

import java.util.List;

/**
 * Result of list of objects.
 *
 * @author Kingen
 * @since 2020/8/7
 */
public class ListResult<T> extends GenericResult<List<T>> {

    protected ListResult(List<T> data) {
        super(data);
    }
}
