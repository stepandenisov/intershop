package ru.yandex.intershop.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.service.ImageService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ImageServiceIntegrationTest extends BaseServiceIntegrationTest{

    @Autowired
    private ImageService imageService;

    @Autowired
    private ImageService imageRepository;

    @Test
    void getImageByItemId_shouldReturnImage(){
        Image image = new Image(null, 1L, new byte[]{1});
        imageRepository.save(image);

        Optional<Image> actualImage = imageService.getImageByItemId(1L);
        assertTrue(actualImage.isPresent(), "Изображение должно быть");
        assertEquals(1, actualImage.get().getItemId(), "id товара должен быть 1");
    }

    @Test
    void save_shouldSaveImage(){
        Image image = new Image(null, 1L, new byte[]{1});
        imageService.save(image);

        Optional<Image> actualImage = imageRepository.getImageByItemId(1L);
        assertTrue(actualImage.isPresent(), "Изображение должно быть");
        assertEquals(1, actualImage.get().getItemId(), "id товара должен быть 1");
    }

}
