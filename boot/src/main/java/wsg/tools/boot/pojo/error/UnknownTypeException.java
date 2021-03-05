package wsg.tools.boot.pojo.error;

import wsg.tools.internet.common.UnexpectedContentException;

/**
 * Exceptions thrown when the type is unknown.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public class UnknownTypeException extends UnexpectedContentException {

    private static final long serialVersionUID = -7454306106132573083L;

    public UnknownTypeException(Class<?> clazz) {
        super("Unknown type: " + clazz);
    }
}
