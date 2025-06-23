package ru.yandex.intershop.service;

import org.springframework.stereotype.Service;
import ru.yandex.intershop.model.Image;
import ru.yandex.intershop.model.Item;
import ru.yandex.intershop.repository.ItemRepository;

import java.util.Optional;

@Service
public class ItemService {

    private final ItemRepository itemRepository;
    private final ImageService imageService;

    public ItemService (ItemRepository itemRepository, ImageService imageService){
        this.itemRepository = itemRepository;
        this.imageService = imageService;
    }

    public Optional<Item> findItemById(Long id){
        return itemRepository.findById(id);
    }

    public Item saveItemWithImage(Item item, Image image){
        Item savedItem = itemRepository.save(item);
        image.setItem(savedItem);
        imageService.save(image);
        return savedItem;
    }

}
