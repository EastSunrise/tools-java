package wsg.tools.internet.base.repository.support;

import java.util.List;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.common.util.function.BiThrowableFunction;
import wsg.tools.internet.base.NextSupplier;
import wsg.tools.internet.base.page.PageRequest;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.base.repository.PageRepository;
import wsg.tools.internet.base.repository.Repository;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * Common repositories.
 *
 * @author Kingen
 * @since 2021/3/14
 */
public final class Repositories {

    private Repositories() {
    }

    /**
     * Returns a linked repository starting with the given first identifier.
     */
    public static <ID, T extends NextSupplier<ID>> LinkedRepository<ID, T>
    linked(Repository<ID, T> repository, ID first) {
        Objects.requireNonNull(repository);
        return new AbstractLinkedRepository<>(first) {
            @Nonnull
            @Override
            public T findById(ID id) throws NotFoundException, OtherResponseException {
                return repository.findById(id);
            }
        };
    }

    /**
     * Returns a list repository with a given list of identifiers.
     */
    public static <ID, T> ListRepository<ID, T> list(Repository<ID, T> repository, List<ID> ids) {
        Objects.requireNonNull(repository);
        Objects.requireNonNull(ids);
        return new AbstractListRepository<>(ids) {
            @Nonnull
            @Override
            public T findById(ID id) throws NotFoundException, OtherResponseException {
                return repository.findById(id);
            }
        };
    }

    /**
     * Returns a repository whose identifiers are pageable.
     */
    public static <ID, T, P extends PageRequest, R extends PageResult<ID>>
    PageRepository<ID, T> page(Repository<ID, T> repository,
        BiThrowableFunction<P, R, NotFoundException, OtherResponseException> findPage,
        P firstRequest) {
        Objects.requireNonNull(repository);
        Objects.requireNonNull(findPage);
        return new AbstractPageRepository<>(firstRequest) {

            @Nonnull
            @Override
            @SuppressWarnings("unchecked")
            public <PP extends PageRequest, RR extends PageResult<ID>> RR findPageResult(PP request)
                throws NotFoundException, OtherResponseException {
                return (RR) findPage.apply((P) request);
            }

            @Nonnull
            @Override
            public T findById(ID id) throws NotFoundException, OtherResponseException {
                return repository.findById(id);
            }
        };
    }
}
