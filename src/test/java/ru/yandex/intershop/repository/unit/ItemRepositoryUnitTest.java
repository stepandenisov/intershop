package ru.yandex.intershop.repository.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.repository.ItemRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryUnitTest {

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
    }

    @Test
    void findByIdDto_shouldReturnItemDto(){
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item);

        Optional<ItemDto> actualItemDto = itemRepository.findByIdDto(savedItem.getId());

        assertTrue(actualItemDto.isPresent(), "Изображение должно быть");
        assertEquals("description", actualItemDto.get().getDescription(), "Описание должно быть description");
        assertNull(actualItemDto.get().getCount(), "Количество товаров должно отсутсвовать");
    }

    @Test
    void findAllByTitleStartsWithOrDescriptionStartsWithDto_shouldReturnItemsDto(){
        Item item = new Item(null, "title", "description", 12.0);
        itemRepository.save(item);

        List<ItemDto> items = itemRepository.findAllByTitleStartsWithOrDescriptionStartsWithDto("title",
                PageRequest.of(0, 10));

        assertEquals(1, items.size(), "Количество товаров должно быть равно 1");
        assertEquals("description", items.get(0).getDescription(), "Описание должно быть description");
    }

    @Test
    void countAllByTitleStartingWithOrDescriptionStartingWith_shouldReturnCount(){
        Item item = new Item(null, "title", "description", 12.0);
        itemRepository.save(item);

        Long countItems = itemRepository.countAllByTitleStartingWithOrDescriptionStartingWith("title", "title");

        assertEquals(1L, countItems, "Количество товаров должно быть равно 1");
    }

    @Test
    void findAllDto_shouldReturnItems(){
        Item item = new Item(null, "title", "description", 12.0);
        itemRepository.save(item);

        Page<ItemDto> items = itemRepository.findAllDto(PageRequest.of(0, 10));

        assertEquals(1, items.getContent().size(), "Количество товаров должно быть равно 1");
        assertEquals("description", items.getContent().get(0).getDescription(), "Описание должно быть description");
    }

}
