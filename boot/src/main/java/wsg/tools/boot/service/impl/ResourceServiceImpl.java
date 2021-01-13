package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.dao.jpa.mapper.ResourceItemRepository;
import wsg.tools.boot.dao.jpa.mapper.ResourceLinkRepository;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.internet.base.BaseSite;
import wsg.tools.internet.base.exception.SiteStatusException;
import wsg.tools.internet.resource.entity.item.base.IdentifiedItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.YearSupplier;
import wsg.tools.internet.resource.entity.resource.base.FilenameSupplier;
import wsg.tools.internet.resource.entity.resource.base.LengthSupplier;
import wsg.tools.internet.resource.entity.resource.base.PasswordProvider;
import wsg.tools.internet.resource.entity.resource.base.ValidResource;
import wsg.tools.internet.resource.site.intf.RangeRepository;
import wsg.tools.internet.resource.site.intf.ResourceRepository;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * @author Kingen
 * @since 2020/11/13
 */
@Slf4j
@Service
public class ResourceServiceImpl extends BaseServiceImpl implements ResourceService {

    private final ResourceItemRepository itemRepository;
    private final ResourceLinkRepository linkRepository;
    private final TransactionTemplate template;

    @Autowired
    public ResourceServiceImpl(ResourceItemRepository itemRepository, ResourceLinkRepository linkRepository, TransactionTemplate template) {
        this.itemRepository = itemRepository;
        this.linkRepository = linkRepository;
        this.template = template;
    }

    @Override
    public <I extends IdentifiedItem, S extends BaseSite & ResourceRepository<I, Integer>> void importAll(S site) throws SiteStatusException {
        int itemsCount = 0, linksCount = 0;
        String name = site.getName();
        for (I item : site.findAll()) {
            int count = saveItem(item, name);
            if (count >= 0) {
                itemsCount++;
                linksCount += count;
            }
        }
        log.info("Finished importing: {} items, {} links.", itemsCount, linksCount);
    }

    @Override
    public <I extends IdentifiedItem, S extends BaseSite & RangeRepository<I>> void importLatest(S site) throws SiteStatusException {
        int itemsCount = 0, linksCount = 0;
        String name = site.getName();
        int start = itemRepository.findMaxSid(name).orElse(0) + 1;
        List<I> items = site.findAllByRangeClosed(start, null);
        for (I item : items) {
            int count = saveItem(item, name);
            if (count >= 0) {
                itemsCount++;
                linksCount += count;
            }
        }
        log.info("Finished importing: {} items, {} links.", itemsCount, linksCount);
    }

    private <I extends IdentifiedItem> int saveItem(I item, String name) {
        List<ValidResource> resources = item.getResources();
        if (CollectionUtils.isEmpty(resources)) {
            return -1;
        }
        if (itemRepository.findBySiteAndSid(name, item.getId()).isPresent()) {
            return -1;
        }

        ResourceItemEntity itemEntity = new ResourceItemEntity();
        itemEntity.setSite(name);
        itemEntity.setSid(item.getId());
        itemEntity.setUrl(item.getUrl());
        itemEntity.setTitle(item.getTitle());
        itemEntity.setIdentified(false);
        if (item instanceof TypeSupplier) {
            itemEntity.setVideoType(((TypeSupplier) item).getType());
        }
        if (item instanceof YearSupplier) {
            itemEntity.setYear(((YearSupplier) item).getYear());
        }
        if (item instanceof DoubanIdentifier) {
            itemEntity.setDbId(((DoubanIdentifier) item).getDbId());
        }
        if (item instanceof ImdbIdentifier) {
            itemEntity.setImdbId(((ImdbIdentifier) item).getImdbId());
        }

        return Objects.requireNonNull(template.execute(status -> {
            Long itemId = itemRepository.insert(itemEntity).getId();
            List<ResourceLinkEntity> links = new LinkedList<>();
            for (ValidResource resource : resources) {
                ResourceLinkEntity linkEntity = new ResourceLinkEntity();
                linkEntity.setItemId(itemId);
                linkEntity.setTitle(resource.getTitle());
                linkEntity.setUrl(resource.getUrl());
                linkEntity.setType(EnumUtilExt.deserializeAka(resource.getClass(), ResourceType.class));
                if (resource instanceof FilenameSupplier) {
                    linkEntity.setFilename(((FilenameSupplier) resource).getFilename());
                }
                if (resource instanceof LengthSupplier) {
                    linkEntity.setLength(((LengthSupplier) resource).length());
                }
                if (resource instanceof PasswordProvider) {
                    linkEntity.setPassword(((PasswordProvider) resource).getPassword());
                }
                links.add(linkEntity);
            }
            linkRepository.saveAll(links);
            return resources.size();
        }));
    }

    @Override
    public List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId, @Nullable String imdbId) {
        ResourceItemEntity entity = new ResourceItemEntity();
        entity.setDbId(dbId);
        entity.setImdbId(imdbId);
        entity.setTitle(key);
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreCase(true).withMatcher("title", ExampleMatcher.GenericPropertyMatchers.contains());
        return itemRepository.findAll(Example.of(entity, matcher));
    }

    @Override
    public long check(List<ResourceCheckDto> checkDtoList) {
        if (CollectionUtils.isEmpty(checkDtoList)) {
            return 0;
        }
        return checkDtoList.stream().filter(checkDto -> {
            if (checkDto == null) {
                return false;
            }
            if (checkDto.getId() == null) {
                return false;
            }
            ResourceItemEntity entity = new ResourceItemEntity();
            entity.setId(checkDto.getId());
            entity.setDbId(checkDto.getDbId());
            entity.setImdbId(checkDto.getImdbId());
            entity.setIdentified(true);
            itemRepository.updateById(entity);
            return true;
        }).count();
    }

    @Override
    public List<ResourceLinkEntity> getLinks(Collection<Long> itemIds) {
        return linkRepository.findAllByItemIdIsIn(itemIds);
    }
}