package ru.yandex.intershop.controller.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.repository.ImageRepository;
import ru.yandex.intershop.repository.ItemRepository;


public class ImageControllerIntegrationTest extends BaseControllerIntegrationTest {

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Long imageId;

    @BeforeEach
    void customSetUp() {
        itemRepository.save(new Item(null, "title", "description", 1.0))
                .flatMap(item ->
                        imageRepository.save(new Image(null, item.getId(), new byte[]{1}))
                )
                .doOnNext(image -> imageId = image.getId())
                .block();
    }

    @Test
    void image_shouldReturnImageBytes() {
        webTestClient.get()
                .uri("/images/" + imageId.toString())
                .exchange()
                .expectStatus().isOk();
    }
}
