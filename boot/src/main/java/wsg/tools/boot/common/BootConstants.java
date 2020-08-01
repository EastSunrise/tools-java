package wsg.tools.boot.common;

/**
 * Constants.
 *
 * @author Kingen
 * @since 2020/7/27
 */
public class BootConstants {

    public static final int DB_FLAG_NOT_DELETED = 0;
    public static final int DB_FLAG_DELETED = 1;

    public static final String DB_LOGIC_DELETE_NOT_DELETED_EQUATION = "delete_flag = " + BootConstants.DB_FLAG_NOT_DELETED;
    public static final String DB_LOGIC_DELETE_SQL_SET_DELETED = " set delete_flag = " + BootConstants.DB_FLAG_DELETED + " where id = ?";
}
