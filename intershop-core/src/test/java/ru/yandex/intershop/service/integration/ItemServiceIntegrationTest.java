package ru.yandex.intershop.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import ru.yandex.intershop.model.Paging;
import ru.yandex.intershop.model.Sorting;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.service.ItemService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceIntegrationTest extends BaseServiceIntegrationTest{

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void findItemByIdDto_shouldReturnItemDto(){
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item).block();

        assertNotNull(savedItem);

        Optional<ItemDto> actualItemDto = itemService.findItemByIdDto(savedItem.getId()).blockOptional();

        assertTrue(actualItemDto.isPresent(), "Изображение должно быть");
        assertEquals("description", actualItemDto.get().getDescription(), "Описание должно быть description");
        assertNull(actualItemDto.get().getCount(), "Количество товаров должно отсутсвовать");
    }


    @Test
    void findItemById_shouldReturnItem(){
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item).block();

        assertNotNull(savedItem);

        Optional<Item> actualItem = itemService.findItemById(savedItem.getId()).blockOptional();

        assertTrue(actualItem.isPresent(), "Изображение должно быть");
        assertEquals("description", actualItem.get().getDescription(), "Описание должно быть description");
    }

    @Test
    void saveItemWithImage_shouldReturnSavedItemId(){
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});

        Long actualItemId = itemService.saveItemWithImage(item, image).block();

        assertNotNull(actualItemId, "Изображение должно быть");
    }

    @Test
    void searchPaginatedAndSorted_shouldReturnListOfItemDto(){
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});

        itemService.saveItemWithImage(item, image).block();

        Paging paging = new Paging(1, 10, false, false);

        Page<ItemDto> items = itemService.searchPaginatedAndSorted("", paging, Sorting.NO).block();

        assertNotNull(items, "Элементы должны быть");

        List<ItemDto> itemsList = items.get().toList();

        assertEquals(1, itemsList.size(), "Количество товаров должно быть равно 1");
        assertEquals("description", itemsList.get(0).getDescription(), "Описание должно быть description");
    }

    @Test
    void findAll_shouldReturnListOfItem(){
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});

        itemService.saveItemWithImage(item, image).block();
        
        List<Item> items = itemService.findAll().collectList().block();

        assertNotNull(items, "Элементы должны быть");
    }

}
