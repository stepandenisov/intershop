package ru.yandex.intershop.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.controller.image.ImageController;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.service.ImageService;

import static org.mockito.Mockito.*;

@WebFluxTest(ImageController.class)
public class ImageControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ImageService imageService;

    @Test
    void image_shouldReturnImageBytes() {

        byte[] imageBytes = new byte[]{1};
        Image image = new Image(1L, 1L, imageBytes);

        when(imageService.getImageByItemId(1L)).thenReturn(Mono.just(image));

        webTestClient.get()
                .uri("/images/1")
                .exchange()
                .expectStatus().isOk();
    }

}
