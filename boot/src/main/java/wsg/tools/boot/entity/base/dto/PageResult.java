package wsg.tools.boot.entity.base.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Result with pagination info.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
@Setter
public class PageResult<T> extends Result {

    private List<T> records = new ArrayList<>();

    private long total = 0;

    private long size = 20;

    private long current = 1;

    public PageResult(Page<T> page) {
        Objects.requireNonNull(page);
        this.records = page.getRecords();
        this.total = page.getTotal();
        this.current = page.getCurrent();
        this.size = page.getSize();
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

    public PageResult(Result other) {
        super(other);
    }
}
