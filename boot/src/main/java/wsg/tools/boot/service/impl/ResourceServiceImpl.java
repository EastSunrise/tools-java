package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.dao.jpa.mapper.ResourceItemRepository;
import wsg.tools.boot.dao.jpa.mapper.ResourceLinkRepository;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.entity.item.BaseItem;
import wsg.tools.internet.resource.entity.item.SimpleItem;
import wsg.tools.internet.resource.entity.resource.base.BaseValidResource;
import wsg.tools.internet.resource.entity.resource.valid.*;
import wsg.tools.internet.resource.site.BaseResourceSite;
import wsg.tools.internet.video.entity.douban.base.DoubanIdentifier;
import wsg.tools.internet.video.entity.imdb.base.ImdbIdentifier;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Autowired
    public ResourceServiceImpl(ResourceItemRepository itemRepository, ResourceLinkRepository linkRepository) {
        this.itemRepository = itemRepository;
        this.linkRepository = linkRepository;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public <I extends BaseItem> void importAll(BaseResourceSite<I> site) {
        Set<I> items = site.findAll();
        for (I item : items) {
            if (CollectionUtils.isEmpty(item.getResources())) {
                continue;
            }

            ResourceItemEntity itemEntity = new ResourceItemEntity();
            itemEntity.setUrl(item.getUrl());
            itemEntity.setTitle(item.getTitle());
            itemEntity.setIdentified(false);
            if (item instanceof SimpleItem) {
                itemEntity.setVideoType(((SimpleItem) item).getType());
                itemEntity.setYear(((SimpleItem) item).getYear());
            }
            if (item instanceof DoubanIdentifier) {
                itemEntity.setDbId(((DoubanIdentifier) item).getDbId());
            }
            if (item instanceof ImdbIdentifier) {
                itemEntity.setImdbId(((ImdbIdentifier) item).getImdbId());
            }
            String itemUrl = itemRepository.insert(itemEntity).getUrl();

            item.getResources().stream().filter(resource -> resource instanceof BaseValidResource)
                    .map(resource -> (BaseValidResource) resource).forEach(resource -> {
                ResourceLinkEntity linkEntity = new ResourceLinkEntity();
                linkEntity.setItemUrl(itemUrl);
                linkEntity.setTitle(resource.getTitle());
                linkEntity.setUrl(resource.getUrl());
                linkEntity.setType(EnumUtilExt.deserializeAka(resource.getClass(), ResourceType.class));
                linkRepository.insert(linkEntity);
            });
        }
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
    public long download(Collection<ResourceLinkEntity> links, File target) {
        Set<BaseValidResource> resources = links.stream().map(link -> {
            switch (link.getType()) {
                case ED2K:
                    return Ed2kResource.of(link.getTitle(), link.getUrl());
                case HTTP:
                    return HttpResource.of(link.getTitle(), link.getUrl());
                case MAGNET:
                    return MagnetResource.of(link.getTitle(), link.getUrl());
                case UC_DISK:
                    return YunResource.of(link.getTitle(), link.getUrl());
                case BAIDU_DISK:
                    return PanResource.of(link.getTitle(), link.getUrl());
                default:
                    throw new IllegalArgumentException("Unknown type of resources: " + link.getType());
            }
        }).collect(Collectors.toSet());
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
        return count;
    }
}