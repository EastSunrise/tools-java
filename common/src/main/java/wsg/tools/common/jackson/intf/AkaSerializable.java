package wsg.tools.common.jackson.intf;

/**
 * Indicate what the object is also known as
 *
 * @author Kingen
 * @since 2020/6/19
 */
public interface AkaSerializable<Aka> {
    /**
     * Also known as, similar to {@link #equals(Object)}
     *
     * @param other aka
     * @return if this is also known as {@code other}
     */
    boolean alsoKnownAs(Aka other);
}
