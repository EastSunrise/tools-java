package wsg.tools.boot.pojo.base;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.List;

/**
 * Return result with pagination info.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
public class PageResult<T extends BaseDto> extends Result {

    private final Page<T> page;

    protected PageResult(Page<T> page) {
        super();
        this.page = page;
    }

    /**
     * Obtains a successful instance of {@link PageResult}
     */
    public static <T extends BaseDto> PageResult<T> of(Page<T> page) {
        return new PageResult<>(page);
    }

    /**
     * Obtains a successful instance of {@link PageResult}
     */
    public static <T extends BaseDto> PageResult<T> of(List<T> list) {
        return new PageResult<>(new PageImpl<>(list));
    }

    public ResponseEntity<?> toResponse(PagedResourcesAssembler<T> assembler) {
        if (isSuccess()) {
            PagedModel<EntityModel<T>> pagedModel = assembler.toModel(page);
            return ResponseEntity.ok(new HashMap<>(3) {{
                put("page", pagedModel.getMetadata());
                put("links", pagedModel.getLinks());
                put("data", pagedModel.getContent());
            }});
        } else {
            return new ResponseEntity<>(error(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
