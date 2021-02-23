package wsg.tools.boot.pojo.error;

import wsg.tools.boot.common.NotFoundException;

/**
 * Exceptions thrown when catching a {@link NotFoundException} which should not have been thrown.
 *
 * @author Kingen
 * @since 2021/2/23
 */
public class ShouldExistException extends RuntimeException {

    public ShouldExistException(NotFoundException cause) {
        super(cause);
    }
}
