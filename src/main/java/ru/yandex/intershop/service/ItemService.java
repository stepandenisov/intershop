package ru.yandex.intershop.service;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.yandex.intershop.model.*;
import ru.yandex.intershop.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ImageService imageService;

    public ItemService (ItemRepository itemRepository, ImageService imageService){
        this.itemRepository = itemRepository;
        this.imageService = imageService;
    }

    public Optional<ItemDto> findItemByIdDto(Long id){
        return itemRepository.findByIdDto(id);
    }

    public Optional<Item> findItemById(Long id){
        return itemRepository.findById(id);
    }

    public Item saveItemWithImage(Item item, Image image){
        Item savedItem = itemRepository.save(item);
        image.setItemId(savedItem.getId());
        imageService.save(image);
        return savedItem;
    }

    public List<ItemDto> searchPaginatedAndSorted(String search, Paging paging, Sorting sort) {
        List<ItemDto> items = search.isBlank() ? findAllPaginated(paging, sort) : searchAllPaginated(search, paging, sort);

        long count = search.isBlank()
                ? itemRepository.count()
                : itemRepository.countAllByTitleStartingWithOrDescriptionStartingWith(search, search);

        long totalPageCount = ((count - 1) / paging.getPageSize()) + 1;

        if (paging.getPageNumber() == 1) {
            paging.setHasPrevious(false);
        } else {
            paging.setHasPrevious(totalPageCount != 1);
        }
        paging.setHasNext(totalPageCount > paging.getPageNumber());
        return items;
    }

    private List<ItemDto> searchAllPaginated(String search, Paging paging, Sorting sorting) {
        return itemRepository.findAllByTitleStartsWithOrDescriptionStartsWithDto(search,
                PageRequest.of(paging.getPageNumber() - 1,
                        paging.getPageSize(),
                        Sort.by(sorting.field)));
    }

    private List<ItemDto> findAllPaginated(Paging paging, Sorting sorting) {
        return itemRepository.findAllDto(PageRequest.of(paging.getPageNumber() - 1,
                        paging.getPageSize(),
                        Sort.by(sorting.field)))
                .stream().toList();
    }

}
