package wsg.tools.internet.common;

import java.io.IOException;

/**
 * Exceptions thrown when the target is not found.
 *
 * @author Kingen
 * @since 2021/2/20
 */
public class NotFoundException extends IOException {

    private static final long serialVersionUID = 5856760359416898520L;

    public NotFoundException(String message) {
        super(message);
    }
}
