package wsg.tools.boot.dao.jpa.mapper;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import org.springframework.stereotype.Repository;
import wsg.tools.boot.dao.jpa.base.BaseRepository;
import wsg.tools.boot.pojo.entity.adult.JaAdultActressEntity;

/**
 * The repository of adult actresses.
 *
 * @author Kingen
 * @since 2021/3/5
 */
@Repository
public interface JaAdultActressRepository extends BaseRepository<JaAdultActressEntity, Long> {

    /**
     * Retrieves actresses by the specified names.
     *
     * @param names names to be queried
     * @return list of actresses matching the specified names
     */
    List<JaAdultActressEntity> findAllByNameIn(List<String> names);

    /**
     * Retrieves an actress by the specified name.
     *
     * @param name the name of the actress to be queried
     * @return {@code Optional#empty()} if not found, otherwise the actress
     */
    Optional<JaAdultActressEntity> findByName(@Nonnull String name);
}
