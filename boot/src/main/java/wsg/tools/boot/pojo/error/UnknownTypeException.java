package wsg.tools.boot.pojo.error;

import wsg.tools.internet.base.exception.UnexpectedContentException;

/**
 * Exceptions thrown when the type is unknown.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public class UnknownTypeException extends UnexpectedContentException {

    public UnknownTypeException(Class<?> clazz) {
        super("Unknown type: " + clazz);
    }
}
