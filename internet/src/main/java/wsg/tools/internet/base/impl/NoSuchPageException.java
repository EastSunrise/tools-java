package wsg.tools.internet.base.impl;

import java.util.NoSuchElementException;

/**
 * Thrown when the {@link wsg.tools.internet.base.intf.PageRequest} being requested does not exist.
 *
 * @author Kingen
 * @since 2021/3/8
 */
public class NoSuchPageException extends NoSuchElementException {

    private static final long serialVersionUID = 1406750608163057387L;

    NoSuchPageException(String s) {
        super(s);
    }
}
