package ru.yandex.intershop.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import ru.yandex.intershop.model.Paging;
import ru.yandex.intershop.model.Sorting;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.service.ItemService;
import ru.yandex.intershop.service.integration.BaseServiceIntegrationTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItemServiceIntegrationTest extends BaseServiceIntegrationTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ReactiveRedisTemplate<String, List<ItemDto>> itemListRedisTemplate;

    @Autowired
    private ReactiveRedisTemplate<String, ItemDto> itemRedisTemplate;

    private static final String ITEM_LIST_CACHE_KEY = "items:";
    private static final String ITEM_CACHE_KEY = "item:";

    @Test
    void findItemByIdDto_shouldReturnItemDto(){
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item).block();

        assertNotNull(savedItem);

        Optional<ItemDto> actualItemDto = itemService.findItemByIdDto(savedItem.getId(), 1L).blockOptional();

        assertTrue(actualItemDto.isPresent(), "Изображение должно быть");
        ItemDto cachedItem = itemRedisTemplate.opsForValue().get(ITEM_CACHE_KEY+actualItemDto.get().getId()).block();
        assertNotNull(cachedItem, "Товар должен быть");
        assertEquals(actualItemDto.get().getId(), cachedItem.getId(), "Id должны совпадать");
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

        Page<ItemDto> items = itemService.searchPaginatedAndSortedForUserById("", paging, Sorting.NO, 1L).block();

        assertNotNull(items, "Элементы должны быть");

        List<ItemDto> itemsList = items.get().toList();

        PageRequest pageable = PageRequest.of(paging.getPageNumber() - 1,
                paging.getPageSize(),
                Sort.by(Sorting.NO.field));
        String key = ITEM_LIST_CACHE_KEY + 1L + ":" + pageable.getPageNumber() + ":" + pageable.getPageSize() + ":" + pageable.getSort();
        List<ItemDto> cachedItems = itemListRedisTemplate.opsForValue().get(key).block();
        assertNotNull(cachedItems, "Товары должны быть");
        assertEquals(cachedItems.get(0).getId(), itemsList.get(0).getId(), "Id должны совпадать");

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
