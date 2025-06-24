package ru.yandex.intershop.repository.unit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.repository.ImageRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class ImageRepositoryUnitTest {
    @Autowired
    private ImageRepository imageRepository;

    @BeforeEach
    public void setUp() {
        imageRepository.deleteAll();
    }

    @Test
    void findById_shouldReturnCart(){
        Image image = new Image(null, 1L, new byte[]{1});
        imageRepository.save(image);
        Optional<Image> actualImage = imageRepository.findImageByItemId(1L);

        assertTrue(actualImage.isPresent(), "Изображение должно быть");
        assertEquals(1L, actualImage.get().getItemId(), "id товара должен быть 1");
    }

}
