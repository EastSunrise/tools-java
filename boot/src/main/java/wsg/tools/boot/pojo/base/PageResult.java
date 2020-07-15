package wsg.tools.boot.pojo.base;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Result with pagination info.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
public class PageResult<T extends BaseDto> extends Result {

    private long total;
    private int page;
    private int size;
    private List<T> records;

    public PageResult(Page<T> page) {
        this.page = page.getNumber();
        this.size = page.getSize();
        this.total = page.getTotalElements();
        this.records = page.getContent();
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
}
