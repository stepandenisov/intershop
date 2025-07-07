package ru.yandex.intershop.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.*;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.repository.ItemRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ImageService imageService;

    public ItemService(ItemRepository itemRepository, ImageService imageService) {
        this.itemRepository = itemRepository;
        this.imageService = imageService;
    }

    public Mono<ItemDto> findItemByIdDto(Long id) {
        return itemRepository.findByIdDto(id);
    }

    public Mono<Item> findItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Mono<Long> saveItemWithImage(Item item, Image image) {
        return itemRepository.save(item)
                .flatMap(savedItem -> {
                    System.out.println("HERE");
                    image.setItemId(savedItem.getId());
                    return imageService.save(image)
                            .flatMap(savedImage -> Mono.just(savedImage.getItemId()));
                });
    }

    public Flux<ItemDto> searchPaginatedAndSorted(String search, Paging paging, Sorting sort) {
        Flux<ItemDto> items = search.isBlank() ? findAllPaginated(paging, sort) : searchAllPaginated(search, paging, sort);
        Mono<Long> count = search.isBlank()
                ? itemRepository.count()
                : itemRepository.countAllByTitleStartingWithOrDescriptionStartingWith(search, search);

        return count.flatMapMany(cnt -> {
            long totalPageCount = (cnt - 1) / paging.getPageSize() + 1;
            if (paging.getPageNumber() == 1) {
                paging.setHasPrevious(false);
            } else {
                paging.setHasPrevious(!Objects.equals(totalPageCount, 1L));
            }
            paging.setHasNext(totalPageCount > paging.getPageNumber());
            return items;
        });
    }

    private Flux<ItemDto> searchAllPaginated(String search, Paging paging, Sorting sorting) {
        return itemRepository.findAllByTitleStartsWithOrDescriptionStartsWithDto(search,
                PageRequest.of(paging.getPageNumber() - 1,
                        paging.getPageSize(),
                        Sort.by(sorting.field)));
    }

    private Flux<ItemDto> findAllPaginated(Paging paging, Sorting sorting) {
        return itemRepository.findAllDto(PageRequest.of(paging.getPageNumber() - 1,
                paging.getPageSize(),
                Sort.by(sorting.field)));
    }

}
