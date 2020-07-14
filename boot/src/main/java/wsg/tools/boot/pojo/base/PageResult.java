package wsg.tools.boot.pojo.base;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * Result with pagination info.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
@Setter
public class PageResult<T extends BaseDto> extends Result {
    private Page<T> records;

    public PageResult(Page<T> records) {
        this.records = records;
    }

    public PageResult(String message) {
        super(message);
    }

    public PageResult(String message, Object[] args) {
        super(message, args);
    }

    public PageResult(Exception e) {
        super(e);
    }

    public PageResult(Result other, Page<T> records) {
        super(other);
        this.records = records;
    }
}
