package wsg.tools.internet.base.repository.support;

import javax.annotation.Nonnull;
import wsg.tools.internet.base.page.PageRequest;
import wsg.tools.internet.base.repository.LinkedRepository;
import wsg.tools.internet.base.repository.PageRepository;
import wsg.tools.internet.base.repository.RepoIterator;

/**
 * This class provides a skeletal implementation of {@link LinkedRepository} and {@link
 * PageRepository} whose first page of identifiers is mapped by {@link #firstRequest}, the first
 * pageable request.
 *
 * @author Kingen
 * @since 2021/3/26
 */
abstract class AbstractPageRepository<ID, T, P extends PageRequest>
    implements PageRepository<ID, T> {

    private final P firstRequest;

    AbstractPageRepository(P firstRequest) {
        this.firstRequest = firstRequest;
    }

    @Nonnull
    @Override
    public RepoIterator<T> repoIterator() {
        return new BasicPageRepoIterator<>(this, firstRequest);
    }
}
