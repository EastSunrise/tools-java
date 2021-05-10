package wsg.tools.boot.dao.api.adapter.impl;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import org.apache.commons.collections4.CollectionUtils;
import wsg.tools.boot.dao.api.adapter.JaAdultActressAdapter;
import wsg.tools.boot.pojo.entity.adult.JaAdultActressEntity;
import wsg.tools.boot.pojo.error.AppException;
import wsg.tools.internet.base.repository.RepoRetrievable;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.info.adult.wiki.BasicInfo;
import wsg.tools.internet.info.adult.wiki.WikiAlbum;
import wsg.tools.internet.info.adult.wiki.WikiAlbumIndex;
import wsg.tools.internet.info.adult.wiki.WikiCelebrity;

/**
 * Adapter that accepts a {@link WikiCelebrity} and returns a {@link JaAdultActressEntity}.
 *
 * @author Kingen
 * @since 2021/4/16
 */
public class CelebrityAdapter implements JaAdultActressAdapter {

    private final WikiCelebrity celebrity;
    private final RepoRetrievable<WikiAlbumIndex, WikiAlbum> retrievable;

    public CelebrityAdapter(WikiCelebrity celebrity,
        RepoRetrievable<WikiAlbumIndex, WikiAlbum> retrievable) {
        this.celebrity = celebrity;
        this.retrievable = retrievable;
    }

    @Nonnull
    @Override
    public JaAdultActressEntity toActress() {
        JaAdultActressEntity entity = new JaAdultActressEntity();
        entity.setName(celebrity.getName());
        BasicInfo info = celebrity.getInfo();
        Set<String> aka = entity.getAka();
        CollectionUtils.addIgnoreNull(aka, info.getFullName());
        aka.addAll(info.getZhNames());
        aka.addAll(info.getJaNames());
        aka.addAll(info.getEnNames());
        aka.addAll(info.getAka());
        entity.setHeight(info.getHeight());
        entity.setWeight(info.getWeight());
        entity.setCup(info.getCup());
        return entity;
    }

    @Nonnull
    @Override
    public List<URL> getImages() {
        List<URL> result = new ArrayList<>();
        result.add(celebrity.getCoverURL());
        for (WikiAlbumIndex index : celebrity.getAlbums()) {
            try {
                result.addAll(retrievable.findById(index).getImages());
            } catch (NotFoundException | OtherResponseException e) {
                throw new AppException(e);
            }
        }
        return result;
    }
}
