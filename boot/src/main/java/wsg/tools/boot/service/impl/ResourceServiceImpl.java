package wsg.tools.boot.service.impl;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;
import wsg.tools.boot.common.enums.ResourceType;
import wsg.tools.boot.common.util.BeanUtilExt;
import wsg.tools.boot.config.MinioConfig;
import wsg.tools.boot.dao.jpa.mapper.ResourceItemRepository;
import wsg.tools.boot.dao.jpa.mapper.ResourceLinkRepository;
import wsg.tools.boot.pojo.dto.LinkDto;
import wsg.tools.boot.pojo.dto.ResourceCheckDto;
import wsg.tools.boot.pojo.entity.base.Source;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity;
import wsg.tools.boot.pojo.entity.resource.ResourceItemEntity_;
import wsg.tools.boot.pojo.entity.resource.ResourceLinkEntity;
import wsg.tools.boot.service.base.BaseServiceImpl;
import wsg.tools.boot.service.intf.ResourceService;
import wsg.tools.common.io.NotFiletypeException;
import wsg.tools.common.lang.EnumUtilExt;
import wsg.tools.internet.base.UpdateDateSupplier;
import wsg.tools.internet.base.UpdateDatetimeSupplier;
import wsg.tools.internet.base.repository.ListRepoIterator;
import wsg.tools.internet.base.repository.ListRepository;
import wsg.tools.internet.common.NotFoundException;
import wsg.tools.internet.common.OtherResponseException;
import wsg.tools.internet.download.FilenameSupplier;
import wsg.tools.internet.download.LengthSupplier;
import wsg.tools.internet.download.Link;
import wsg.tools.internet.download.PasswordProvider;
import wsg.tools.internet.download.Thunder;
import wsg.tools.internet.movie.common.StateSupplier;
import wsg.tools.internet.movie.common.YearSupplier;
import wsg.tools.internet.movie.douban.DoubanIdentifier;
import wsg.tools.internet.movie.imdb.ImdbIdentifier;
import wsg.tools.internet.movie.resource.BaseIdentifiedItem;

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
    private final MinioConfig config;

    @Autowired
    public ResourceServiceImpl(ResourceItemRepository itemRepository,
        ResourceLinkRepository linkRepository, TransactionTemplate template,
        MinioConfig config) {
        this.itemRepository = itemRepository;
        this.linkRepository = linkRepository;
        this.template = template;
        this.config = config;
    }

    @Override
    public <T extends BaseIdentifiedItem>
    void importIntListRepository(@Nonnull ListRepository<Integer, T> repository, String domain)
        throws OtherResponseException {
        Optional<Long> optional = itemRepository.findMaxRid(domain);
        int startIndex = optional.map(Long::intValue).map(repository::indexOf)
            .map(index -> index + 1).orElse(0);
        ListRepoIterator<Integer, T> iterator = repository.listRepoIterator(startIndex);
        int success = 0, total = 0, notFound = 0;
        while (iterator.hasNext()) {
            try {
                T item = iterator.next();
                Source source = Source.record(domain, item.getSubtype(), item.getId());
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
    private <T extends BaseIdentifiedItem>
    int insertItem(T item, Source source) {
        ResourceItemEntity itemEntity = new ResourceItemEntity();
        itemEntity.setSource(source);
        itemEntity.setTitle(item.getTitle());
        itemEntity.setIdentified(false);
        URL cover = item.getCover();
        if (cover != null) {
            try {
                itemEntity.setCover(config.uploadCover(cover, source));
            } catch (NotFoundException | NotFiletypeException | OtherResponseException ignored) {
            }
        }
        if (item instanceof YearSupplier) {
            itemEntity.setYear(((YearSupplier) item).getYear());
        }
        if (item instanceof StateSupplier) {
            itemEntity.setState(((StateSupplier) item).getState());
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
            LocalDate date = ((UpdateDateSupplier) item).lastUpdate();
            itemEntity.setUpdateTime(LocalDateTime.of(date, LocalTime.MIN));
        }
        List<Link> resources = item.getLinks();
        Integer count = template.execute(status -> insertResource(itemEntity, resources));
        return Objects.requireNonNull(count);
    }

    private int insertResource(ResourceItemEntity itemEntity, List<Link> resources) {
        Long itemId = itemRepository.insert(itemEntity).getId();
        List<ResourceLinkEntity> links = new LinkedList<>();
        for (Link resource : resources) {
            ResourceLinkEntity linkEntity = new ResourceLinkEntity();
            linkEntity.setItemId(itemId);
            linkEntity.setTitle(resource.getTitle());
            linkEntity.setUrl(resource.getUrl());
            linkEntity.setType(EnumUtilExt.valueOfAka(ResourceType.class, resource.getClass()));
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
        @Nullable String imdbId, @Nullable Boolean identified) {
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
            itemRepository.updateByIdExceptNull(entity);
            count++;
        }
        return count;
    }

    @Override
    public List<ResourceDto> getResources(Collection<ResourceItemEntity> items) {
        Set<Long> itemIds = items.stream().map(ResourceItemEntity::getId)
            .collect(Collectors.toSet());
        List<ResourceLinkEntity> links = linkRepository.findAllByItemIdIsIn(itemIds);
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