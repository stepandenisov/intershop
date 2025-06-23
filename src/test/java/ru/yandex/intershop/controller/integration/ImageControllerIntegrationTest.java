package ru.yandex.intershop.controller.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.repository.ImageRepository;
import ru.yandex.intershop.repository.ItemRepository;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class ImageControllerIntegrationTest extends BaseControllerIntegrationTest{

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void customSetUp(){
        Item item = new Item(null, "title", "description", 1.0);
        Item savedItem = itemRepository.save(item);
        Image image = new Image(null, savedItem.getId(), new byte[]{1});
        imageRepository.save(image);
    }

    @Test
    void image_shouldReturnImageBytes() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/images/5"))
                .andExpect(status().isOk());
    }
}
