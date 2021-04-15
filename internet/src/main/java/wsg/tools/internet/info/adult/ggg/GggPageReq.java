package wsg.tools.internet.info.adult.ggg;

import javax.annotation.Nonnull;
import org.jetbrains.annotations.Contract;
import wsg.tools.internet.base.page.BasicPageReq;
import wsg.tools.internet.base.view.PathSupplier;

/**
 * A request with pagination information.
 *
 * @author Kingen
 * @since 2021/4/13
 */
public class GggPageReq extends BasicPageReq {

    public static final int DEFAULT_SIZE = 10;
    private static final long serialVersionUID = 7528898370372812068L;

    private final GggCategory category;
    private final Order order;

    public GggPageReq(int current, @Nonnull GggCategory category, @Nonnull Order order) {
        super(current, DEFAULT_SIZE);
        this.category = category;
        this.order = order;
    }

    @Nonnull
    @Contract("_ -> new")
    public static GggPageReq byDate(GggCategory category) {
        return new GggPageReq(0, category, Order.DATE);
    }

    public GggCategory getCategory() {
        return category;
    }

    public Order getOrder() {
        return order;
    }

    @Override
    public GggPageReq next() {
        return new GggPageReq(super.next().getCurrent(), category, order);
    }

    @Override
    public GggPageReq previous() {
        return new GggPageReq(super.previous().getCurrent(), category, order);
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
