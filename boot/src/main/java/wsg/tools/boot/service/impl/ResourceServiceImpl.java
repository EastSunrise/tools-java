package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.dao.jpa.mapper.ResourceItemRepository;
import wsg.tools.boot.dao.jpa.mapper.ResourceLinkRepository;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.pojo.result.SingleResult;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.lang.AssertUtils;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.internet.base.SiteStatus;
import wsg.tools.internet.base.exception.SiteStatusException;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.item.base.BaseItem;
import wsg.tools.internet.resource.entity.item.base.TypeSupplier;
import wsg.tools.internet.resource.entity.item.base.YearSupplier;
import wsg.tools.internet.resource.entity.resource.ResourceFactory;
import wsg.tools.internet.resource.entity.resource.base.*;
import wsg.tools.internet.resource.site.BaseResourceSite;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * @author Kingen
 * @since 2020/11/13
 */
@Slf4j
@Service
public class ResourceServiceImpl extends BaseServiceImpl implements ResourceService {

    private final Thunder thunder = new Thunder();
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
    public <I extends BaseItem> void importAll(BaseResourceSite<I> site) throws SiteStatusException {
        SiteStatus annotation = site.getClass().getAnnotation(SiteStatus.class);
        if (annotation != null) {
            SiteStatus.Status status = annotation.status();
            if (!SiteStatus.Status.NORMAL.equals(status)) {
                throw new SiteStatusException(annotation);
            }
        }
        List<I> items = site.findAll();
        int itemsCount = 0, linksCount = 0;
        for (I item : items) {
            List<ValidResource> resources = item.getResources();
            if (CollectionUtils.isEmpty(resources)) {
                continue;
            }
            String itemUrl = item.getUrl();
            if (itemRepository.findByUrl(itemUrl).isPresent()) {
                continue;
            }

            ResourceItemEntity itemEntity = new ResourceItemEntity();
            itemEntity.setUrl(itemUrl);
            itemEntity.setSite(site.getName());
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

            List<ResourceLinkEntity> links = new LinkedList<>();
            for (ValidResource resource : resources) {
                ResourceLinkEntity linkEntity = new ResourceLinkEntity();
                linkEntity.setItemUrl(itemUrl);
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
                linksCount++;
            }

            template.execute(status -> {
                ResourceItemEntity insert = itemRepository.insert(itemEntity);
                linkRepository.saveAll(links);
                return insert;
            });
            itemsCount++;
        }
        log.info("Finished importing: {} items, {} links.", itemsCount, linksCount);
    }

    @Override
    public Set<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId, @Nullable String imdbId) {
        Set<ResourceItemEntity> entities = new HashSet<>();
        if (StringUtils.isNotBlank(key)) {
            entities.addAll(itemRepository.findAllByTitleLike("%" + key + "%"));
        }
        if (dbId != null) {
            entities.addAll(itemRepository.findAllByDbId(dbId));
        }
        if (StringUtils.isNotBlank(imdbId)) {
            entities.addAll(itemRepository.findAllByImdbId(imdbId));
        }
        return entities;
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
            if (StringUtils.isBlank(checkDto.getUrl())) {
                return false;
            }
            ResourceItemEntity entity = new ResourceItemEntity();
            entity.setUrl(checkDto.getUrl());
            entity.setDbId(checkDto.getDbId());
            entity.setImdbId(checkDto.getImdbId());
            entity.setIdentified(true);
            itemRepository.updateById(entity);
            return true;
        }).count();
    }

    @Override
    public SingleResult<Long> download(File target, @Nullable Long dbId, @Nullable String imdbId) {
        Set<ResourceItemEntity> items = search(null, dbId, imdbId);
        if (items.isEmpty()) {
            return SingleResult.of(-1L);
        }
        Set<ValidResource> resources = new HashSet<>();
        for (ResourceItemEntity item : items) {
            if (!item.getIdentified()) {
                continue;
            }
            List<ResourceLinkEntity> links = linkRepository.findAllByItemUrl(item.getUrl());
            links.stream().map(link -> {
                try {
                    return ResourceFactory.create(link.getTitle(), link.getUrl(), link.getPassword());
                } catch (InvalidResourceException e) {
                    throw AssertUtils.runtimeException(e);
                }
            }).forEach(resources::add);
        }

        long count = resources.stream().filter(resource -> {
            try {
                return thunder.addTask(target, resource);
            } catch (IOException e) {
                log.error(e.getMessage());
                return false;
            }
        }).count();
        if (count > 0) {
            log.info("{} resources added to download.", count);
        }
        return SingleResult.of(count);
    }

    @Override
    public List<ResourceLinkEntity> getLinks(Collection<String> itemUrls) {
        return linkRepository.findAllByItemUrlIsIn(itemUrls);
    }
}