package ru.yandex.intershop.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.service.ImageService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ImageServiceIntegrationTest extends BaseServiceIntegrationTest{

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageService imageRepository;

    private Long itemId;

    @Test
    void saveAndGetImageByItemId_shouldSaveAndReturnImage(){
        Item item = new Item(null, "title", "description", 12.0);
        itemRepository.save(item)
                        .flatMap(item1 -> {
                            itemId = item1.getId();
                            Image image = new Image(null, itemId, new byte[]{1});
                            return imageService.save(image);
                        })
                .block();

        Optional<Image> actualImage = imageService.getImageByItemId(itemId).blockOptional();
        assertTrue(actualImage.isPresent(), "Изображение должно быть");
        assertEquals(itemId, actualImage.get().getItemId(), "id товара должен быть " + itemId);
    }

}
