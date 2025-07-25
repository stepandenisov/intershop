package ru.yandex.intershop.controller.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.reactive.function.BodyInserters;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.service.auth.AuthService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;


public class ItemControllerIntegrationTest extends BaseControllerIntegrationTest{

    @Autowired
    private ItemRepository itemRepository;

    private Long itemId;

    @BeforeEach
    void customSetUp(){
        Item item = new Item(null, "title", "description", 1.0);
        itemRepository.save(item)
                .doOnNext(item1 -> itemId = item1.getId())
                .block();
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void getAddItemPage_shouldReturnAddItemView() {
        webTestClient.get()
                .uri("/items/add")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML);
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void post_shouldReturnHtmlWithItem() {
        var builder = new MultipartBodyBuilder();
        builder.part("title", "Test");
        builder.part("description", "Test");
        builder.part("price", "12.0");
        builder.part("image", new ByteArrayResource(new byte[] { 1, 2, 3, 4 }) {
            @Override
            public String getFilename() {
                return "image.png";
            }
        });
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/items/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void items_shouldReturnHtmlWithItems() {
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

    @Test
    void item_shouldReturnHtmlWithItem() {
        webTestClient.get()
                .uri("/items/"+itemId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("<form"));
                });
    }
}
