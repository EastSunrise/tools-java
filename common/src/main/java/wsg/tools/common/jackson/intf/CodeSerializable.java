package wsg.tools.common.jackson.intf;

/**
 * Serialize to a code, usually stored in the database.
 *
 * @author Kingen
 * @since 2020/6/17
 */
public interface CodeSerializable<T> {
    /**
     * Serialize to a code
     *
     * @return code serialized
     */
    T getCode();
}
