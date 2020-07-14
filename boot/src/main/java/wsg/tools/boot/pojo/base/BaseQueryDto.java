package wsg.tools.boot.pojo.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Base class for objects of query conditions.
 *
 * @author Kingen
 * @since 2020/6/24
 */
@Setter
@Getter
public class BaseQueryDto extends PageRequest {

    /**
     * Creates a new {@link PageRequest} with sort parameters applied.
     *
     * @param page zero-based page index, must not be negative.
     * @param size the size getInstance the page to be returned, must be greater than 0.
     * @param sort must not be {@literal null}, use {@link Sort#unsorted()} instead.
     */
    public BaseQueryDto(int page, int size, Sort sort) {
        super(page, size, sort);
    }

    public BaseQueryDto() {
        super(0, 20, Sort.unsorted());
    }
}
