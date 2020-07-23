package wsg.tools.common.excel.writer;

/**
 * Interface to write a numeric cell.
 *
 * @author Kingen
 * @since 2020/7/23
 */
public abstract class NumericCellFromGetter<T, V> extends CellFromGetter<T, V> {

    @Override
    protected Class<?> getMiddleType() {
        return Number.class;
    }
}
