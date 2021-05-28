package wsg.tools.boot.dao.api.support;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;
import wsg.tools.boot.dao.jpa.mapper.IdRelationRepository;
import wsg.tools.boot.pojo.entity.subject.IdRelationEntity;
import wsg.tools.internet.base.page.Page;
import wsg.tools.internet.base.page.PageIndex;
import wsg.tools.internet.common.LoginException;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.movie.common.enums.DoubanMark;
import wsg.tools.internet.movie.douban.AbstractMovie;
import wsg.tools.internet.movie.douban.DoubanBook;
import wsg.tools.internet.movie.douban.DoubanCatalog;
import wsg.tools.internet.movie.douban.DoubanRepository;
import wsg.tools.internet.movie.douban.MarkedSubject;
import wsg.tools.internet.movie.douban.PersonIndex;
import wsg.tools.internet.movie.douban.SubjectIndex;

/**
 * Interceptor that handles the data retrieves from {@link DoubanRepository}.
 *
 * @author Kingen
 * @since 2021/5/22
 */
class DoubanInterceptor implements DoubanRepository, Closeable {

    private final DoubanRepository doubanRepo;
    private final IdRelationRepository relationRepository;

    DoubanInterceptor(DoubanRepository doubanRepo, IdRelationRepository relationRepository) {
        this.doubanRepo = doubanRepo;
        this.relationRepository = relationRepository;
    }

    @Override
    public Long user() {
        return doubanRepo.user();
    }

    @Override
    public void login(String username, String password)
        throws OtherResponseException, LoginException {
        doubanRepo.login(username, password);
    }

    @Override
    public void logout() throws OtherResponseException {
        doubanRepo.logout();
    }

    @Nonnull
    @Override
    public Page<SubjectIndex> searchGlobally(String keyword, @Nonnull PageIndex page,
        @Nullable DoubanCatalog catalog) throws OtherResponseException {
        return doubanRepo.searchGlobally(keyword, page, catalog);
    }

    @Nonnull
    @Override
    public List<SubjectIndex> search(@Nonnull DoubanCatalog catalog, String keyword)
        throws OtherResponseException {
        return doubanRepo.search(catalog, keyword);
    }

    @Nonnull
    @Override
    public Page<MarkedSubject> findUserSubjects(@Nonnull DoubanCatalog catalog,
        long userId, @Nonnull DoubanMark mark, @Nonnull PageIndex page)
        throws NotFoundException, OtherResponseException {
        return doubanRepo.findUserSubjects(catalog, userId, mark, page);
    }

    @Nonnull
    @Override
    public Page<PersonIndex> findUserCreators(@Nonnull DoubanCatalog catalog, long userId,
        @Nonnull PageIndex page) throws NotFoundException, OtherResponseException {
        return doubanRepo.findUserCreators(catalog, userId, page);
    }

    /**
     * Saves the id relation if exists.
     */
    @Override
    public AbstractMovie findMovieById(long id) throws NotFoundException, OtherResponseException {
        AbstractMovie movie = doubanRepo.findMovieById(id);
        if (movie.getImdbId() != null) {
            saveIdRelation(id, movie.getImdbId());
        }
        return movie;
    }

    @Override
    public DoubanBook findBookById(long id) throws NotFoundException, OtherResponseException {
        return doubanRepo.findBookById(id);
    }

    @Override
    public long getDbIdByImdbId(String imdbId)
        throws NotFoundException, OtherResponseException, LoginException {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isPresent()) {
            return optional.get().getDbId();
        }
        return saveIdRelation(doubanRepo.getDbIdByImdbId(imdbId), imdbId);
    }

    private long saveIdRelation(long dbId, String imdbId) {
        Optional<IdRelationEntity> optional = relationRepository.findById(imdbId);
        if (optional.isEmpty()) {
            IdRelationEntity entity = new IdRelationEntity();
            entity.setImdbId(imdbId);
            entity.setDbId(dbId);
            relationRepository.insert(entity);
        }
        return dbId;
    }

    @Override
    public void close() throws IOException {
        if (doubanRepo instanceof Closeable) {
            ((Closeable) doubanRepo).close();
        }
    }
}
