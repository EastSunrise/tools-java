package wsg.tools.boot.pojo.base;

/**
 * Exceptions when data is lacking.
 *
 * @author Kingen
 * @since 2020/11/22
 */
public class DataIntegrityException extends Exception {

    public DataIntegrityException(String message) {
        super(message);
    }
}
