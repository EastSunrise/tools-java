package wsg.tools.boot.pojo.base;

import lombok.Getter;

import java.util.List;
import java.util.Objects;

/**
 * Result of list of records.
 *
 * @author Kingen
 * @since 2020/8/7
 */
public class ListResult<T> extends BaseResult {

    @Getter
    private final List<T> records;

    private ListResult(List<T> records) {
        this.records = Objects.requireNonNull(records);
    }

    public static <T> ListResult<T> of(List<T> data) {
        return new ListResult<>(data);
    }
}
