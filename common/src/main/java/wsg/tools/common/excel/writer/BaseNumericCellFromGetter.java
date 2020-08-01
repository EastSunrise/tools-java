package wsg.tools.common.excel.writer;

/**
 * Interface to write a numeric cell.
 *
 * @author Kingen
 * @since 2020/7/23
 */
public abstract class BaseNumericCellFromGetter<T, V> extends BaseCellFromGetter<T, V> {

    @Override
    protected Class<?> getMiddleType() {
        return Number.class;
    }
}
