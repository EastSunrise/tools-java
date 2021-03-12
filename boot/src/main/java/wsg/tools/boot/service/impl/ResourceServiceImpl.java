package wsg.tools.boot.service.impl;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.NotFoundException;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.common.util.OtherHttpResponseException;
import wsg.tools.boot.common.util.SiteUtilExt;
import wsg.tools.boot.dao.jpa.mapper.FailureRepository;
import wsg.tools.boot.dao.jpa.mapper.ResourceItemRepository;
import wsg.tools.boot.dao.jpa.mapper.ResourceLinkRepository;
import wsg.tools.boot.pojo.dto.LinkDto;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.base.Failure;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity_;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.common.util.function.IntCodeSupplier;
import wsg.tools.internet.base.intf.IntRangeIdentifiedRepository;
import wsg.tools.internet.base.intf.LinkedRepository;
import wsg.tools.internet.base.intf.RepositoryIterator;
import wsg.tools.internet.common.NextSupplier;
import wsg.tools.internet.common.UpdateDateSupplier;
import wsg.tools.internet.common.UpdateDatetimeSupplier;
import wsg.tools.internet.download.base.AbstractLink;
import wsg.tools.internet.download.base.FilenameSupplier;
import wsg.tools.internet.download.base.LengthSupplier;
import wsg.tools.internet.download.base.PasswordProvider;
import wsg.tools.internet.download.impl.Thunder;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;
import wsg.tools.internet.resource.common.YearSupplier;
import wsg.tools.internet.resource.movie.IdentifiedItem;

/**
 * @author Kingen
 * @since 2020/11/13
 */
@Slf4j
@Service
public class ResourceServiceImpl extends BaseServiceImpl implements ResourceService {

    private static final String ITEM_EXISTS_MSG = "The resource item exists";
    private final ResourceItemRepository itemRepository;
    private final ResourceLinkRepository linkRepository;
    private final FailureRepository failureRepository;
    private final TransactionTemplate template;

    @Autowired
    public ResourceServiceImpl(ResourceItemRepository itemRepository,
        ResourceLinkRepository linkRepository,
        FailureRepository failureRepository,
        TransactionTemplate template) {
        this.itemRepository = itemRepository;
        this.linkRepository = linkRepository;
        this.failureRepository = failureRepository;
        this.template = template;
    }

    @Override
    public <E extends Enum<E> & IntCodeSupplier, T extends IdentifiedItem<E> & NextSupplier<Integer>>
    void importLinkedRepository(LinkedRepository<Integer, T> repository, String domain, int subtype)
        throws OtherHttpResponseException {
        Optional<Long> optional = itemRepository.findMaxRid(domain, subtype);
        RepositoryIterator<T> iterator;
        if (optional.isPresent()) {
            int start = Math.toIntExact(optional.get());
            iterator = repository.iteratorAfter(start);
            SiteUtilExt.found(iterator::next);
        } else {
            iterator = repository.iterator();
        }
        int success = 0, failure = 0;
        while (iterator.hasNext()) {
            T item = SiteUtilExt.found(iterator::next);
            Source source = Source.record(domain, subtype, item.getId());
            if (insertItem(item, source) >= 0) {
                success++;
            } else {
                failure++;
            }
        }
        log.info("Imported resources from {}: {} succeed, {} failed", domain, success, failure);
    }

    @Override
    public <E extends Enum<E> & IntCodeSupplier, T extends IdentifiedItem<E>>
    void importIntRangeRepository(IntRangeIdentifiedRepository<T> repository, String domain)
        throws OtherHttpResponseException {
        Optional<Long> optional = itemRepository.findMaxRid(domain);
        int start = optional.map(Long::intValue).map(id -> id + 1).orElse(repository.min());
        RepositoryIterator<T> iterator = repository.iteratorAfter(start);
        int success = 0, total = 0, notFound = 0;
        while (iterator.hasNext()) {
            try {
                T item = SiteUtilExt.ifNotFound(iterator::next, "A record of " + domain);
                Integer subtype = item.getSubtype().getCode();
                Source source = Source.record(domain, subtype, item.getId());
                if (insertItem(item, source) >= 0) {
                    success++;
                }
            } catch (NotFoundException e) {
                notFound++;
            }
            total++;
        }
        log.info("Imported resources from {}: {} succeed, {} failed, {} not found", domain, success,
            total - success - notFound, notFound);
    }

    /**
     * @return the count of inserted links, or -1 if the item exists.
     */
    private <E extends Enum<E> & IntCodeSupplier, T extends IdentifiedItem<E>>
    int insertItem(T item, Source source) {
        List<AbstractLink> resources = item.getLinks();
        if (itemRepository.findBySource(source).isPresent()) {
            failureRepository.insert(new Failure(source, ITEM_EXISTS_MSG));
            return -1;
        }

        ResourceItemEntity itemEntity = new ResourceItemEntity();
        itemEntity.setSource(source);
        itemEntity.setTitle(item.getTitle());
        itemEntity.setIdentified(false);
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
            itemEntity
                .setUpdateTime(
                    LocalDateTime.of(((UpdateDateSupplier) item).lastUpdate(), LocalTime.MIN));
        }
        return Objects
            .requireNonNull(template.execute(status -> insertResource(itemEntity, resources)));
    }

    private int insertResource(ResourceItemEntity itemEntity, List<AbstractLink> resources) {
        Long itemId = itemRepository.insert(itemEntity).getId();
        List<ResourceLinkEntity> links = new LinkedList<>();
        for (AbstractLink resource : resources) {
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
    }

    @Override
    public List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId,
        @Nullable String imdbId) {
        return search(key, dbId, imdbId, null);
    }

    @Override
    public List<ResourceItemEntity> search(@Nullable String key, @Nullable Long dbId,
        @Nullable String imdbId,
        @Nullable Boolean identified) {
        Specification<ResourceItemEntity> specification = (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (dbId != null) {
                predicates.add(builder.equal(root.get(ResourceItemEntity_.dbId), dbId));
            }
            if (StringUtils.isNotBlank(imdbId)) {
                predicates.add(builder.equal(root.get(ResourceItemEntity_.imdbId), imdbId));
            }
            if (StringUtils.isNotBlank(key)) {
                predicates.add(like(builder, root.get(ResourceItemEntity_.title), key));
            }
            Predicate predicate = builder.or(predicates.toArray(new Predicate[1]));
            if (identified != null) {
                predicate = builder
                    .and(predicate,
                        builder.equal(root.get(ResourceItemEntity_.identified), identified));
            }
            return query.where(predicate).getRestriction();
        };
        return itemRepository.findAll(specification);
    }

    @Override
    public long identifyResources(List<ResourceCheckDto> checkDtoList) {
        if (CollectionUtils.isEmpty(checkDtoList)) {
            return 0L;
        }
        long count = 0L;
        for (ResourceCheckDto checkDto : checkDtoList) {
            if (checkDto == null) {
                continue;
            }
            if (checkDto.getId() == null) {
                continue;
            }
            ResourceItemEntity entity = new ResourceItemEntity();
            entity.setId(checkDto.getId());
            entity.setDbId(checkDto.getDbId());
            entity.setImdbId(checkDto.getImdbId());
            entity.setIdentified(true);
            itemRepository.updateById(entity);
            count++;
        }
        return count;
    }

    @Override
    public List<ResourceDto> getResources(Collection<ResourceItemEntity> items) {
        List<ResourceLinkEntity> links = linkRepository
            .findAllByItemIdIsIn(
                items.stream().map(ResourceItemEntity::getId).collect(Collectors.toSet()));
        Map<Long, List<ResourceLinkEntity>> linkMap =
            links.stream().collect(Collectors.groupingBy(ResourceLinkEntity::getItemId));
        List<ResourceDto> resources = new ArrayList<>();
        for (ResourceItemEntity item : items) {
            ResourceDto resource = new ResourceDto(item.getTitle(), item.getIdentified());
            List<ResourceLinkEntity> linkEntities = linkMap.get(item.getId());
            if (linkEntities != null) {
                List<LinkDto> linkDtos = new ArrayList<>();
                for (ResourceLinkEntity link : linkEntities) {
                    LinkDto linkDto = BeanUtilExt.convert(link, LinkDto.class);
                    if (linkDto.getFilename() == null) {
                        linkDto.setFilename(linkDto.getTitle());
                    }
                    linkDto.setThunder(Thunder.encodeThunder(linkDto.getUrl()));
                    linkDtos.add(linkDto);
                }
                resource.setLinks(linkDtos);
            }
            resources.add(resource);
        }
        return resources;
    }
}