package ru.yandex.intershop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.*;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.repository.ItemRepository;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ImageService imageService;

    private final ReactiveRedisTemplate<String, List<ItemDto>> itemListRedisTemplate;

    private final ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate;

    private static final String ITEM_LIST_CACHE_KEY = "items:";
    private static final String ITEM_CACHE_KEY = "item:";


    public ItemService(ItemRepository itemRepository,
                       ImageService imageService,
                       ReactiveRedisTemplate<String, List<ItemDto>> itemListRedisTemplate,
                       ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate) {
        this.itemRepository = itemRepository;
        this.imageService = imageService;
        this.itemListRedisTemplate = itemListRedisTemplate;
        this.itemRedisTemplate = itemRedisTemplate;
    }

    public Mono<ItemDto> findItemByIdDto(Long id, Long userId) {
        return itemRedisTemplate.opsForValue().get(ITEM_CACHE_KEY + userId + ":" + id)
                .switchIfEmpty(itemRepository.findByIdDtoJoinCountOnUserId(id, userId)
                        .filter(Objects::nonNull)
                        .flatMap(itemDto -> itemRedisTemplate.opsForValue().set(ITEM_CACHE_KEY + id, itemDto, Duration.ofMinutes(1))
                                .thenReturn(itemDto)));
    }

    public Mono<Void> flushCacheByUserId(Long userId) {
        return itemRedisTemplate.opsForValue().delete(ITEM_CACHE_KEY + userId)
                .then();
    }

    public Mono<Void> flushListCachesByUserId(Long userId) {
        return itemListRedisTemplate.scan()
                .filter(key -> key.startsWith(ITEM_LIST_CACHE_KEY + userId))
                .flatMap(itemListRedisTemplate::delete)
                .then();
    }

    public Mono<Item> findItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Mono<Long> saveItemWithImage(Item item, Image image) {
        return itemRepository.save(item)
                .flatMap(savedItem -> {
                    image.setItemId(savedItem.getId());
                    return imageService.save(image)
                            .flatMap(savedImage -> Mono.just(savedImage.getItemId()));
                });
    }

    public Mono<Page<ItemDto>> searchPaginatedAndSortedForUserById(String search, Paging paging, Sorting sort, Long userId) {
        Flux<ItemDto> items = search.isBlank()
                ? findAllPaginatedForUserById(paging, sort, userId)
                : searchAllPaginatedForUserById(search, paging, sort, userId);
        Mono<Long> countByCondition = search.isBlank()
                ? itemRepository.count()
                : itemRepository.countAllByTitleStartingWithOrDescriptionStartingWith(search, search);

        Mono<Long> count = countByCondition.doOnNext(cnt -> {
            long totalPageCount = (cnt - 1) / paging.getPageSize() + 1;
            if (paging.getPageNumber() == 1) {
                paging.setHasPrevious(false);
            } else {
                paging.setHasPrevious(!Objects.equals(totalPageCount, 1L));
            }
            paging.setHasNext(totalPageCount > paging.getPageNumber());
        });
        PageRequest pageRequest = PageRequest.of(paging.getPageNumber() - 1,
                paging.getPageSize(),
                Sort.by(sort.field));
        return Mono.zip(items.collectList(), count)
                .map(p -> new PageImpl<>(p.getT1(), pageRequest, p.getT2()));
    }

    private Flux<ItemDto> searchAllPaginatedForUserById(String search, Paging paging, Sorting sorting, Long userId) {
        PageRequest pageable = PageRequest.of(paging.getPageNumber() - 1,
                paging.getPageSize(),
                Sort.by(sorting.field));
        String key = ITEM_LIST_CACHE_KEY + userId + ":" + search + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize() + ":" + pageable.getSort();
        Flux<ItemDto> searchItems = itemRepository.findAllByTitleStartsWithOrDescriptionStartsWithDtoSortByOffsetLimit(pageable, search, userId)
                .collectList()
                .flatMap(items ->
                        itemListRedisTemplate.opsForValue()
                                .set(key, items, Duration.ofMinutes(1))
                                .thenReturn(items))
                .flatMapMany(Flux::fromIterable);
        return itemListRedisTemplate.opsForValue()
                .get(key)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(searchItems);
    }

    private Flux<ItemDto> findAllPaginatedForUserById(Paging paging, Sorting sorting, Long userId) {
        PageRequest pageable = PageRequest.of(paging.getPageNumber() - 1,
                paging.getPageSize(),
                Sort.by(sorting.field));
        String key = ITEM_LIST_CACHE_KEY + userId + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize() + ":" + pageable.getSort();

        Flux<ItemDto> findItems = itemRepository.findAllDtoSortByOffsetLimit(pageable, userId)
                .collectList()
                .flatMap(items ->
                        itemListRedisTemplate.opsForValue()
                                .set(key, items, Duration.ofMinutes(1))
                                .thenReturn(items))
                .flatMapMany(Flux::fromIterable);

        return itemListRedisTemplate.opsForValue()
                .get(key)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(findItems);
    }

    public Flux<Item> findAll() {
        return itemRepository.findAll();
    }

}
