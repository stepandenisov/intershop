package ru.yandex.intershop.service.unit;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.intershop.model.Paging;
import ru.yandex.intershop.model.Sorting;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.service.ImageService;
import ru.yandex.intershop.service.ItemService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemServiceUnitTest {

    @InjectMocks
    private ItemService itemService;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private ImageService imageService;

    @Test
    void findItemByIdDto_shouldReturnItemDto(){
        ItemDto itemDto = new ItemDto(1L, "title", "description", 12.0, 1);
        doReturn(Optional.of(itemDto)).when(itemRepository).findByIdDto(1L);

        Optional<ItemDto> actualItemDto = itemService.findItemByIdDto(1L);

        assertTrue(actualItemDto.isPresent(), "Товар должен быть");
        assertEquals("description", actualItemDto.get().getDescription(), "Описание должно быть description");
        assertEquals(1, actualItemDto.get().getCount(), "Количество товаров должно быть 1");
    }


    @Test
    void findItemById_shouldReturnItem(){
        Item item = new Item(1L, "title", "description", 12.0);
        doReturn(Optional.of(item)).when(itemRepository).findById(1L);

        Optional<Item> actualItem = itemService.findItemById(1L);

        assertTrue(actualItem.isPresent(), "Товар должен быть");
        assertEquals("description", actualItem.get().getDescription(), "Описание должно быть description");
    }

    @Test
    void saveItemWithImage_shouldReturnSavedItem(){
        Item item = new Item(1L, "title", "description", 12.0);
        Image image = new Image(1L, null, new byte[]{1});

        doReturn(item).when(itemRepository).save(item);
        doNothing().when(imageService).save(image);

        Item actualItem = itemService.saveItemWithImage(item, image);

        assertNotNull(actualItem, "Изображение должно быть");
        assertEquals("description", actualItem.getDescription(), "Описание должно быть description");
    }
}
