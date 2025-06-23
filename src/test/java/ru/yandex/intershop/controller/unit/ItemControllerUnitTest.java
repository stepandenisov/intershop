package ru.yandex.intershop.controller.unit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.controller.image.ImageController;
import ru.yandex.intershop.controller.item.ItemController;
import ru.yandex.intershop.model.Paging;
import ru.yandex.intershop.model.Sorting;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.service.ImageService;
import ru.yandex.intershop.service.ItemService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(ItemController.class)
public class ItemControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getAddItemPage_shouldReturnAddItemView() throws Exception {

        mockMvc.perform(get("/items/add"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("add-item"));
    }

    @Test
    void post_shouldReturnHtmlWithItem() throws Exception {
        MockMultipartFile imageFile = new MockMultipartFile("image", "", "application/json", "..........".getBytes());
        MockPart titlePart = new MockPart("title", "Test".getBytes());
        MockPart descriptionPart = new MockPart("description", "Test".getBytes());
        MockPart pricePart = new MockPart("price", "12.0".getBytes());

        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, "..........".getBytes());

        Item newItem = new Item(1L, "title", "description", 12.0);

        when(itemService.saveItemWithImage(item, image)).thenReturn(newItem);

        mockMvc.perform(multipart("/items/")
                        .file(imageFile)
                        .part(titlePart)
                        .part(descriptionPart)
                        .part(pricePart))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));
    }


    @Test
    void item_shouldReturnHtmlWithItem() throws Exception {

        ItemDto itemDto = new ItemDto(1L, "title", "description", 12.0, 1);

        when(itemService.findItemByIdDto(1L)).thenReturn(Optional.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items/1"))
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

        Paging paging = new Paging(1, 10, false, false);
        Sorting sort = Sorting.NO;
        String search = "";

        ItemDto itemDto = new ItemDto(1L, "title", "description", 12.0, 1);

        when(itemService.searchPaginatedAndSorted(search, paging, sort))
                .thenReturn(List.of(itemDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("items"))
                .andExpect(model().attributeExists("items"))
                .andExpect(xpath("//table/tr").nodeCount(6))
                .andExpect(xpath("//table/tr[2]/td/table/tr[2]/td/b").string("title"));
    }

}
