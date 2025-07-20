package ru.yandex.intershop.service.unit;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.repository.ImageRepository;
import ru.yandex.intershop.service.ImageService;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class ImageServiceUnitTest {

    @Autowired
    private ImageService imageService;

    @MockitoBean
    private ImageRepository imageRepository;

    @MockitoBean
    private ReactiveRedisTemplate<String, Image> imageReactiveRedisTemplate;

    @MockitoBean
    private ReactiveValueOperations<String, Image> valueOperations;

    @Test
    void getImageByItemId_shouldReturnImage(){
        Image image = new Image(1L, 1L, new byte[]{1});

        String key = "image:"+1L;
        when(imageReactiveRedisTemplate.opsForValue()).thenReturn(valueOperations);
        when(valueOperations.get(key)).thenReturn(Mono.just(image));
        when(imageRepository.findImageByItemId(1L)).thenReturn(Mono.just(image));
        Optional<Image> actualImage = imageService.getImageByItemId(1L).blockOptional();
        assertTrue(actualImage.isPresent(), "Изображение должно быть");
        assertEquals(1, actualImage.get().getItemId(), "id товара должен быть 1");
    }

}
