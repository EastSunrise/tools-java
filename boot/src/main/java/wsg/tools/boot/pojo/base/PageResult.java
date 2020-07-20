package wsg.tools.boot.pojo.base;

import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;

import java.util.List;

/**
 * Return result with pagination info.
 *
 * @author Kingen
 * @since 2020/7/10
 */
@Getter
public class PageResult<T extends BaseDto> extends Result {

    private Page<T> page;

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

    @Override
    public ResponseEntity<?> toResponse() {
        put("page", new PagedModel.PageMetadata(page.getSize(), page.getNumber(), page.getTotalElements()));
        put("data", page.getContent());
        return super.toResponse();
    }

    public ResponseEntity<?> toResponse(PagedResourcesAssembler<T> assembler) {
        PagedModel<EntityModel<T>> pagedModel = assembler.toModel(page);
        put("page", pagedModel.getMetadata());
        put("links", pagedModel.getLinks());
        put("data", pagedModel.getContent());
        return super.toResponse();
    }
}
