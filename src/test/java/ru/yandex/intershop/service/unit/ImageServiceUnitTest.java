package ru.yandex.intershop.service.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.repository.CartRepository;
import ru.yandex.intershop.repository.ImageRepository;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ImageService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ImageServiceUnitTest {

    @InjectMocks
    private ImageService imageService;

    @Mock
    private ImageRepository imageRepository;

    @Test
    void getImageByItemId_shouldReturnImage(){
        Image image = new Image(1L, 1L, new byte[]{1});

        doReturn(Optional.of(image)).when(imageRepository).findImageByItemId(1L);

        Optional<Image> actualImage = imageService.getImageByItemId(1L);
        assertTrue(actualImage.isPresent(), "Изображение должно быть");
        assertEquals(1, actualImage.get().getItemId(), "id товара должен быть 1");
    }

}
