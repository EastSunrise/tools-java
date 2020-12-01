package wsg.tools.common.io.multimedia;

/**
 * If an error occurs when parsing the output stream of a command task.
 *
 * @author Kingen
 * @since 2020/12/2
 */
public class ParseException extends Exception {

    public ParseException(String message) {
        super(message);
    }
}
