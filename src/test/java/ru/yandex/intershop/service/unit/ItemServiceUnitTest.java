package ru.yandex.intershop.service.unit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.service.ImageService;
import ru.yandex.intershop.service.ItemService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemServiceUnitTest {

    @Autowired
    private ItemService itemService;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private ImageService imageService;

    @Test
    void findItemByIdDto_shouldReturnItemDto(){
        ItemDto itemDto = new ItemDto(1L, "title", "description", 12.0, 1);
        when(itemRepository.findByIdDto(1L)).thenReturn(Mono.just(itemDto));

        Optional<ItemDto> actualItemDto = itemService.findItemByIdDto(1L).blockOptional();

        assertTrue(actualItemDto.isPresent(), "Товар должен быть");
        assertEquals("description", actualItemDto.get().getDescription(), "Описание должно быть description");
        assertEquals(1, actualItemDto.get().getCount(), "Количество товаров должно быть 1");
    }


    @Test
    void findItemById_shouldReturnItem(){
        Item item = new Item(1L, "title", "description", 12.0);
        when(itemRepository.findById(1L)).thenReturn(Mono.just(item));

        Optional<Item> actualItem = itemService.findItemById(1L).blockOptional();

        assertTrue(actualItem.isPresent(), "Товар должен быть");
        assertEquals("description", actualItem.get().getDescription(), "Описание должно быть description");
    }

    @Test
    void saveItemWithImage_shouldReturnSavedItem(){
        Item item = new Item(1L, "title", "description", 12.0);
        Image image = new Image(1L, null, new byte[]{1});

        when(itemRepository.save(any(Item.class))).thenReturn(Mono.just(item));
        when(imageService.save(any(Image.class))).thenReturn(Mono.just(image));

        Long actualItemId = itemService.saveItemWithImage(item, image).block();

        assertNotNull(actualItemId, "ID товара должен быть");
    }
}
