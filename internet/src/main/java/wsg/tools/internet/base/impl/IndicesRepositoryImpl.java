package wsg.tools.internet.base.impl;

import java.util.Collection;
import javax.annotation.Nonnull;
import wsg.tools.internet.base.intf.IndicesRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.base.intf.RepositoryIterator;

/**
 * Base implementation of {@link IndicesRepository}.
 * <p>
 * The iteration of the repository will go on with that of the {@link #indices}.
 *
 * @author Kingen
 * @since 2021/3/15
 */
public class IndicesRepositoryImpl<I, T> implements IndicesRepository<T> {

    private final Repository<I, T> repository;
    private final Collection<I> indices;

    IndicesRepositoryImpl(@Nonnull Repository<I, T> repository, @Nonnull Collection<I> indices) {
        this.repository = repository;
        this.indices = indices;
    }

    @Override
    public RepositoryIterator<T> iterator() {
        return new IdentifiedIteratorImpl<>(repository, indices.iterator());
    }
}
