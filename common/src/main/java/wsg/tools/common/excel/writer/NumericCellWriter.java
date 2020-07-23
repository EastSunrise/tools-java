package wsg.tools.common.excel.writer;

/**
 * Interface to write a numeric cell.
 *
 * @author Kingen
 * @since 2020/7/23
 */
@FunctionalInterface
public interface NumericCellWriter<T, V> extends CellWriter<T, V> {

    /**
     * Returns target type to write into a numeric cell.
     *
     * @return target type
     */
    @Override
    default Class<?> getMiddleType() {
        return Number.class;
    }
}
