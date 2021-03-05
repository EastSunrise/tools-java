package wsg.tools.boot.pojo.result;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import wsg.tools.boot.pojo.dto.BaseDto;

/**
 * Result with pagination info.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
public final class PageResult<T extends BaseDto> extends BaseResult {

    private final Page<T> page;

    private PageResult(Page<T> page) {
        super();
        this.page = page;
    }

    /**
     * Obtains a successful instance.
     */
    public static <T extends BaseDto> PageResult<T> of(Page<T> page) {
        return new PageResult<>(page);
    }

    /**
     * Obtains a successful instance.
     */
    public static <T extends BaseDto> PageResult<T> of(List<T> list) {
        return new PageResult<>(new PageImpl<>(list));
    }

    public ResponseEntity<?> toResponse(PagedResourcesAssembler<T> assembler) {
        PagedModel<EntityModel<T>> pagedModel = assembler.toModel(page);
        return ResponseEntity
            .ok(Map.of("page", Objects.requireNonNull(pagedModel.getMetadata()), "links",
                pagedModel.getLinks(), "data", pagedModel.getContent()));
    }
}
