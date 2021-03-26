package wsg.tools.internet.base.repository.support;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.page.PageRequest;
import wsg.tools.internet.base.page.PageResult;
import wsg.tools.internet.base.repository.PageRepository;
import wsg.tools.internet.base.repository.RepoIterator;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;

/**
 * A pageable implementation of {@link RepoIterator}.
 *
 * @author Kingen
 * @since 2021/3/18
 */
class BasicPageRepoIterator<ID, T, P extends PageRequest, R extends PageResult<ID>>
    implements RepoIterator<T> {

    private final PageRepository<ID, T> repository;
    private Iterator<ID> content = Collections.emptyIterator();
    private P request;

    BasicPageRepoIterator(PageRepository<ID, T> repository, P firstRequest) {
        this.repository = Objects.requireNonNull(repository);
        this.request = firstRequest;
    }

    @Override
    public boolean hasNext() {
        return content.hasNext() || request != null;
    }

    @Nonnull
    @Override
    @SuppressWarnings("unchecked")
    public T next() throws NotFoundException, OtherResponseException {
        if (!content.hasNext()) {
            if (request == null) {
                throw new NoSuchElementException("Doesn't have next page");
            }
            R result = repository.findPageResult(request);
            request = result.hasNext() ? (P) result.nextPageRequest() : null;
            content = result.getContent().iterator();
        }
        if (content.hasNext()) {
            return repository.findById(content.next());
        }
        throw new NoSuchElementException("Doesn't have more content");
    }
}
