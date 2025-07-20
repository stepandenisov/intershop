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

    public Mono<ItemDto> findItemByIdDto(Long id) {
        return itemRedisTemplate.opsForValue().get(ITEM_CACHE_KEY + id)
                .switchIfEmpty(itemRepository.findByIdDto(id)
                        .flatMap(itemDto -> itemRedisTemplate.opsForValue().set(ITEM_CACHE_KEY + id, itemDto, Duration.ofMinutes(1))
                                .then(Mono.just(itemDto))));
    }

    public Mono<Void> flushCacheById(Long id) {
        return itemRedisTemplate.opsForValue().delete(ITEM_CACHE_KEY+id)
                .then();
    }
    public Mono<Void> flushListCaches() {
        return itemListRedisTemplate.scan()
                .filter(key -> key.startsWith(ITEM_LIST_CACHE_KEY))
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

    public Mono<Page<ItemDto>> searchPaginatedAndSorted(String search, Paging paging, Sorting sort) {
        Flux<ItemDto> items = search.isBlank() ? findAllPaginated(paging, sort) : searchAllPaginated(search, paging, sort);
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

    private Flux<ItemDto> searchAllPaginated(String search, Paging paging, Sorting sorting) {
        PageRequest pageable = PageRequest.of(paging.getPageNumber() - 1,
                paging.getPageSize(),
                Sort.by(sorting.field));
        String key = ITEM_LIST_CACHE_KEY + search + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize() + ":" + pageable.getSort();
        return itemListRedisTemplate.opsForValue()
                .get(key)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(itemRepository.findAllByTitleStartsWithOrDescriptionStartsWithDtoSortByOffsetLimit(pageable, search)
                        .collectList()
                        .flatMap(items ->
                                itemListRedisTemplate.opsForValue()
                                        .set(key, items, Duration.ofMinutes(1))
                                        .thenReturn(items)
                        )
                        .flatMapMany(Flux::fromIterable));
    }

    private Flux<ItemDto> findAllPaginated(Paging paging, Sorting sorting) {
        PageRequest pageable = PageRequest.of(paging.getPageNumber() - 1,
                paging.getPageSize(),
                Sort.by(sorting.field));
        String key = ITEM_LIST_CACHE_KEY + pageable.getPageNumber() + ":" + pageable.getPageSize() + ":" + pageable.getSort();
        return itemListRedisTemplate.opsForValue()
                .get(key)
                .flatMapMany(Flux::fromIterable)
                .switchIfEmpty(itemRepository.findAllDtoSortByOffsetLimit(pageable)
                        .collectList()
                        .flatMap(items ->
                                itemListRedisTemplate.opsForValue()
                                        .set(key, items, Duration.ofMinutes(1))
                                        .thenReturn(items)
                        )
                        .flatMapMany(Flux::fromIterable));
    }

    public Flux<Item> findAll() {
        return itemRepository.findAll();
    }

}
