package wsg.tools.boot.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpResponseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.dao.jpa.mapper.ResourceItemRepository;
import wsg.tools.boot.dao.jpa.mapper.ResourceLinkRepository;
import wsg.tools.boot.pojo.dto.LinkDto;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity_;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.internet.base.RecordIterator;
import wsg.tools.internet.base.intf.RangeRepository;
import wsg.tools.internet.base.intf.Repository;
import wsg.tools.internet.resource.base.AbstractResource;
import wsg.tools.internet.resource.base.FilenameSupplier;
import wsg.tools.internet.resource.base.LengthSupplier;
import wsg.tools.internet.resource.base.PasswordProvider;
import wsg.tools.internet.resource.download.Thunder;
import wsg.tools.internet.resource.item.IdentifiedItem;
import wsg.tools.internet.resource.item.intf.TypeSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDateSupplier;
import wsg.tools.internet.resource.item.intf.UpdateDatetimeSupplier;
import wsg.tools.internet.resource.item.intf.YearSupplier;
import wsg.tools.internet.video.site.douban.DoubanIdentifier;
import wsg.tools.internet.video.site.imdb.ImdbIdentifier;

import javax.annotation.Nullable;
import javax.persistence.criteria.Predicate;
import java.util.*;
import java.util.stream.Collectors;

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
    public <T extends IdentifiedItem> void importIterator(RecordIterator<T> iterator, String repositoryId) throws HttpResponseException {
        int itemsCount = 0, linksCount = 0;
        while (iterator.hasNext()) {
            int count = saveItem(iterator.next(), repositoryId);
            if (count >= 0) {
                itemsCount++;
                linksCount += count;
            }

        }
        log.info("Finished importing resources of {}: {} items, {} links.", repositoryId, itemsCount, linksCount);
    }

    @Override
    public <T extends IdentifiedItem, R extends Repository<Integer, T> & RangeRepository<T, Integer>>
    void importRangeRepository(R repository, String repositoryId) throws HttpResponseException {
        int itemsCount = 0, linksCount = 0;
        int start = itemRepository.findMaxSid(repositoryId).orElse(repository.min());
        List<T> items = repository.findAllByRangeClosed(start, repository.max());
        for (T item : items) {
            int count = saveItem(item, repositoryId);
            if (count >= 0) {
                itemsCount++;
                linksCount += count;
            }
        }
        log.info("Finished importing: {} items, {} links.", itemsCount, linksCount);
    }

    private <T extends IdentifiedItem> int saveItem(T item, String domain) {
        List<AbstractResource> resources = item.getResources();
        if (itemRepository.findBySiteAndSid(domain, item.getId()).isPresent()) {
            return -1;
        }

        ResourceItemEntity itemEntity = new ResourceItemEntity();
        itemEntity.setSite(domain);
        itemEntity.setSid(item.getId());
        itemEntity.setUrl(item.getUrl());
        itemEntity.setTitle(item.getTitle());
        itemEntity.setIdentified(false);
        if (item instanceof TypeSupplier) {
            itemEntity.setType(((TypeSupplier) item).getType());
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
        if (item instanceof UpdateDatetimeSupplier) {
            itemEntity.setUpdateTime(((UpdateDatetimeSupplier) item).lastUpdate());
        } else if (item instanceof UpdateDateSupplier) {
            itemEntity.setUpdateTime(((UpdateDateSupplier) item).lastUpdate());
        }

        return Objects.requireNonNull(template.execute(status -> {
            Long itemId = itemRepository.insert(itemEntity).getId();
            List<ResourceLinkEntity> links = new LinkedList<>();
            for (AbstractResource resource : resources) {
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
        return search(key, dbId, imdbId, null);
    }

    @Override
    public List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId, @Nullable String imdbId, @Nullable Boolean identified) {
        Specification<ResourceItemEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dbId != null) {
                predicates.add(builder.equal(root.get(ResourceItemEntity_.dbId), dbId));
            }
            if (StringUtils.isNotBlank(imdbId)) {
                predicates.add(builder.equal(root.get(ResourceItemEntity_.imdbId), imdbId));
            }
            if (StringUtils.isNotBlank(key)) {
                predicates.add(builder.like(root.get(ResourceItemEntity_.title), "%" + key + "%", '%'));
            }
            Predicate predicate = builder.or(predicates.toArray(new Predicate[0]));
            if (identified != null) {
                predicate = builder.and(predicate, builder.equal(root.get(ResourceItemEntity_.identified), identified));
            }
            return query.where(predicate).getRestriction();
        };
        return itemRepository.findAll(specification);
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
    public List<ResourceDto> getResources(Collection<ResourceItemEntity> items) {
        List<ResourceLinkEntity> links = linkRepository.findAllByItemIdIsIn(items.stream().map(ResourceItemEntity::getId).collect(Collectors.toSet()));
        Map<Long, List<ResourceLinkEntity>> linkMap = links.stream().collect(Collectors.groupingBy(ResourceLinkEntity::getItemId));
        return items.stream().map(item -> {
            ResourceDto resource = new ResourceDto(item.getTitle(), item.getUrl(), item.getIdentified());
            List<ResourceLinkEntity> linkEntities = linkMap.get(item.getId());
            if (linkEntities != null) {
                resource.setLinks(linkEntities.stream().map(link -> {
                    LinkDto linkDto = BeanUtilExt.convert(link, LinkDto.class);
                    if (linkDto.getFilename() == null) {
                        linkDto.setFilename(linkDto.getTitle());
                    }
                    linkDto.setThunder(Thunder.encodeThunder(linkDto.getUrl()));
                    return linkDto;
                }).collect(Collectors.toList()));
            }
            return resource;
        }).collect(Collectors.toList());
    }
}