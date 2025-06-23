package ru.yandex.intershop.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.controller.image.ImageController;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ImageService;
import ru.yandex.intershop.service.OrderService;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ImageController.class)
public class ImageControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ImageService imageService;

    @Test
    void image_shouldReturnImageBytes() throws Exception {

        byte[] imageBytes = new byte[]{1};
        Image image = new Image(1L, 1L, imageBytes);

        when(imageService.getImageByItemId(1L)).thenReturn(Optional.of(image));

        mockMvc.perform(MockMvcRequestBuilders.get("/images/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM));
    }

}
