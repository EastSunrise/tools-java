package wsg.tools.internet.resource.item.intf;

/**
 * Supply the state of the item.
 * <p>
 * todo update snapshots based on the state.
 *
 * @author Kingen
 * @since 2021/2/10
 */
public interface StateSupplier {

    /**
     * Obtains the state.
     *
     * @return state
     */
    String getState();
}
