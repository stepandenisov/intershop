package ru.yandex.intershop.repository.unit;


import org.junit.jupiter.api.Test;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ImageRepositoryUnitTest extends BaseRepositoryUnitTest{


    @Test
    void findById_shouldReturnCart(){
        Item item = itemRepository.save(new Item(null, "Test", "Description", 1.0)).block();
        assertNotNull(item);
        imageRepository.save(new Image(null, item.getId(), new byte[]{1})).block();
        Optional<Image> actualImage = imageRepository.findImageByItemId(item.getId()).blockOptional();

        assertTrue(actualImage.isPresent(), "Изображение должно быть");
        assertEquals(item.getId(), actualImage.get().getItemId(), "id товара должен быть " + item.getId());
    }

}
