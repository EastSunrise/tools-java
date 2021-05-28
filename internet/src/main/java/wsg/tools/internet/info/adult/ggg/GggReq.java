package wsg.tools.internet.info.adult.ggg;

import java.io.Serializable;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * A request with pagination information.
 *
 * @author Kingen
 * @since 2021/4/13
 */
public class GggReq implements Serializable {

    private static final long serialVersionUID = 7528898370372812068L;

    private final GggCategory category;
    private final Order order;

    public GggReq(@Nonnull GggCategory category, @Nonnull Order order) {
        this.category = category;
        this.order = order;
    }

    @Nonnull
    @Contract("_ -> new")
    public static GggReq byDate(GggCategory category) {
        return new GggReq(category, Order.DATE);
    }

    public GggCategory getCategory() {
        return category;
    }

    public Order getOrder() {
        return order;
    }

    public enum Order implements PathSupplier {
        CODE("goodsId"),
        DATE("buildDate"),
        SALES("buyCount"),
        CLICKS("clickCount");

        private final String text;

        Order(String text) {
            this.text = text;
        }

        @Override
        public String getAsPath() {
            return text;
        }
    }
}
