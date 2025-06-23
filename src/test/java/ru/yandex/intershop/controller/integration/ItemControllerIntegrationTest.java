package ru.yandex.intershop.controller.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.repository.ItemRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


public class ItemControllerIntegrationTest extends BaseControllerIntegrationTest{

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void customSetUp(){
        Item item = new Item(null, "title", "description", 1.0);
        itemRepository.save(item);
    }

    @Test
    void getAddItemPage_shouldReturnAddItemView() throws Exception {
        mockMvc.perform(get("/items/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-item"));
    }

    @Test
    void post_shouldReturnHtmlWithItem() throws Exception {
        MockMultipartFile image = new MockMultipartFile("image", "", "application/json", "..........".getBytes());
        MockPart titlePart = new MockPart("title", "Test".getBytes());
        MockPart descriptionPart = new MockPart("description", "Test".getBytes());
        MockPart pricePart = new MockPart("price", "12.0".getBytes());
        mockMvc.perform(multipart("/items/")
                        .file(image)
                        .part(titlePart)
                        .part(descriptionPart)
                        .part(pricePart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/3"));
    }


    @Test
    void item_shouldReturnHtmlWithItem() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items/5"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"))
                .andExpect(xpath("//div").nodeCount(1))
                .andExpect(xpath("//div/p").nodeCount(3))
                .andExpect(xpath("//div/p[2]/b[1]").string("title"));
    }

    @Test
    void items_shouldReturnHtmlWithItems() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(xpath("//table/tr").nodeCount(6))
                .andExpect(xpath("//table/tr[2]/td/table/tr[2]/td/b").string("title"));
    }
}
