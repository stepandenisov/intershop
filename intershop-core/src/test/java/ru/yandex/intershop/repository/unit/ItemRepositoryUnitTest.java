package ru.yandex.intershop.repository.unit;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ItemRepositoryUnitTest extends BaseRepositoryUnitTest{

    @Test
    void findByIdDto_shouldReturnItemDto(){
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item).block();

        assertNotNull(savedItem);

        Optional<ItemDto> actualItemDto = itemRepository.findByIdDtoJoinCountOnUserId(savedItem.getId(), 1L).blockOptional();

        assertTrue(actualItemDto.isPresent(), "Изображение должно быть");
        assertEquals("description", actualItemDto.get().getDescription(), "Описание должно быть description");
        assertNull(actualItemDto.get().getCount(), "Количество товаров должно отсутсвовать");
    }

    @Test
    void findAllByTitleStartsWithOrDescriptionStartsWithDto_shouldReturnItemsDto(){
        Item item = new Item(null, "title", "description", 12.0);
        itemRepository.save(item).block();

        List<ItemDto> items = itemRepository.findAllByTitleStartsWithOrDescriptionStartsWithDtoSortByOffsetLimit(
                PageRequest.of(0, 10), "title", 1L).collectList().block();
        assertNotNull(items);

        assertEquals(1, items.size(), "Количество товаров должно быть равно 1");
        assertEquals("description", items.get(0).getDescription(), "Описание должно быть description");
    }

    @Test
    void countAllByTitleStartingWithOrDescriptionStartingWith_shouldReturnCount(){
        Item item = new Item(null, "title", "description", 12.0);
        itemRepository.save(item).block();

        Long countItems = itemRepository.countAllByTitleStartingWithOrDescriptionStartingWith("title", "title").block();

        assertEquals(1L, countItems, "Количество товаров должно быть равно 1");
    }

    @Test
    void findAllDto_shouldReturnItems(){
        Item item = new Item(null, "title", "description", 12.0);
        itemRepository.save(item).block();

        List<ItemDto> items = itemRepository.findAllDtoSortByOffsetLimit(PageRequest.of(0, 10), 1L).collectList().block();

        assertNotNull(items);
        assertEquals(1, items.size(), "Количество товаров должно быть равно 1");
        assertEquals("description", items.get(0).getDescription(), "Описание должно быть description");
    }

}
