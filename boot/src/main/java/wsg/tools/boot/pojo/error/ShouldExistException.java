package wsg.tools.boot.pojo.error;

import wsg.tools.internet.common.NotFoundException;

/**
 * Exceptions thrown when catching a {@link NotFoundException} which should not have been thrown.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public class ShouldExistException extends RuntimeException {

    private static final long serialVersionUID = 3475544961035594631L;

    public ShouldExistException(NotFoundException cause) {
        super(cause);
    }
}
