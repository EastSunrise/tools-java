package wsg.tools.boot.entity.base;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;

import java.util.List;

/**
 * Base class of query object.
 *
 * @author Kingen
 * @since 2020/6/24
 */
@Setter
@Getter
public class BaseQuery {

    private int current = 1;
    private int pageSize = 20;
    private List<String> sorts;
    private List<Boolean> orders;

    /**
     * Obtains an instance of Page from a query object
     */
    public <T> Page<T> getPage() {
        Page<T> page = new Page<>(current, pageSize);
        if (sorts != null) {
            int size = CollectionUtils.isEmpty(orders) ? 0 : orders.size();
            for (int i = 0; i < sorts.size(); i++) {
                if (i < size && orders.get(i) != null && !orders.get(i)) {
                    page.addOrder(OrderItem.desc(sorts.get(i)));
                } else {
                    page.addOrder(OrderItem.asc(sorts.get(i)));
                }
            }
        }
        return page;
    }
}
