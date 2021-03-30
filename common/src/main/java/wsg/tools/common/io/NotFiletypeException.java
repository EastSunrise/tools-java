package wsg.tools.common.io;

import java.io.IOException;

/**
 * Exceptions thrown if the file is not the target type.
 *
 * @author Kingen
 * @since 2021/3/27
 */
public class NotFiletypeException extends IOException {

    private static final long serialVersionUID = 8645826639709446160L;

    public NotFiletypeException(Filetype filetype, String filename) {
        super(String.format("Not a file of type %s: %s", filetype, filename));
    }
}
