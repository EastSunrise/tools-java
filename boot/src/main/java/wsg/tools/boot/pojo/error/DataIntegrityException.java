package wsg.tools.boot.pojo.error;

/**
 * Exceptions thrown when data are lacking.
 *
 * @author Kingen
 * @since 2020/11/22
 */
public class DataIntegrityException extends Exception {

    public DataIntegrityException(String message) {
        super(message);
    }
}
