package ru.yandex.intershop.controller.unit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.controller.item.ItemController;
import ru.yandex.intershop.model.Paging;
import ru.yandex.intershop.model.Sorting;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebFluxTest(ItemController.class)
public class ItemControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private ItemService itemService;

    @Test
    void getAddItemPage_shouldReturnAddItemView() {

        webTestClient.get()
                .uri("/items/add")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML);
    }

    @Test
    void post_shouldReturnHtmlWithItem() {
        var builder = new MultipartBodyBuilder();
        builder.part("title", "Test");
        builder.part("description", "Test");
        builder.part("price", "12.0");

        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, "..........".getBytes());

        Item newItem = new Item(1L, "title", "description", 12.0);

        when(itemService.saveItemWithImage(item, image)).thenReturn(Mono.just(newItem.getId()));

        builder.part("image", new ByteArrayResource(new byte[] { 1, 2, 3, 4 }) {
            @Override
            public String getFilename() {
                return "image.png";
            }
        });
        webTestClient.post()
                .uri("/items/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection();
    }


    @Test
    void item_shouldReturnHtmlWithItem(){

        ItemDto itemDto = new ItemDto(1L, "title", "description", 12.0, 1);

        when(itemService.findItemByIdDto(1L)).thenReturn(Mono.just(itemDto));

        webTestClient.get()
                .uri("/items/1")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<form"));
                });
    }

    @Test
    void items_shouldReturnHtmlWithItems() {

        Paging paging = new Paging(1, 10, false, false);
        Sorting sort = Sorting.NO;
        String search = "";

        ItemDto itemDto = new ItemDto(1L, "title", "description", 12.0, 1);

        when(itemService.searchPaginatedAndSorted(search, paging, sort))
                .thenReturn(Mono.just(new PageImpl<>(List.of(itemDto))));

        webTestClient.get()
                .uri("/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<table"));
                });
    }

}
