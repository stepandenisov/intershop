package ru.yandex.intershop.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
        Item savedItem = itemRepository.save(item);

        Optional<ItemDto> actualItemDto = itemService.findItemByIdDto(savedItem.getId());

        assertTrue(actualItemDto.isPresent(), "Изображение должно быть");
        assertEquals("description", actualItemDto.get().getDescription(), "Описание должно быть description");
        assertNull(actualItemDto.get().getCount(), "Количество товаров должно отсутсвовать");
    }


    @Test
    void findItemById_shouldReturnItem(){
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item);

        Optional<Item> actualItem = itemService.findItemById(savedItem.getId());

        assertTrue(actualItem.isPresent(), "Изображение должно быть");
        assertEquals("description", actualItem.get().getDescription(), "Описание должно быть description");
    }

    @Test
    void saveItemWithImage_shouldReturnSavedItem(){
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});

        Item actualItem = itemService.saveItemWithImage(item, image);

        assertNotNull(actualItem, "Изображение должно быть");
        assertEquals("description", actualItem.getDescription(), "Описание должно быть description");
    }

    @Test
    void searchPaginatedAndSorted_shouldReturnListOfItemDto(){
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});

        itemService.saveItemWithImage(item, image);

        Paging paging = new Paging(1, 10, false, false);

        List<ItemDto> items = itemService.searchPaginatedAndSorted("", paging, Sorting.NO);

        assertEquals(1, items.size(), "Количество товаров должно быть равно 1");
        assertEquals("description", items.get(0).getDescription(), "Описание должно быть description");
    }

}
