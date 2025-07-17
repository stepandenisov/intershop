package ru.yandex.intershop.controller.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@DirtiesContext
public class OrderControllerIntegrationTest extends BaseControllerIntegrationTest{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Long orderId;

    @BeforeEach
    void customSetUp(){
        Mono<Order> orderMono = orderRepository.save(new Order(null, 1.0, new ArrayList<>()));
        Mono<Item> itemMono = itemRepository.save(new Item(null, "title", "description", 1.0));
        Mono.zip(orderMono, itemMono)
                .flatMap(tuple -> {
                    Item item = tuple.getT2();
                    Order order = tuple.getT1();
                    orderId = order.getId();
                    OrderItem orderItem = new OrderItem(null, order.getId(), item.getId(), 1, 1.0, item);
                    order.setOrderItems(List.of(orderItem));
                    return orderRepository.save(order);
                }).block();

    }

    @Test
    void orders_shouldReturnHtmlWithOrders() {
        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("1.0 руб."));
                });
    }

    @Test
    void order_shouldReturnHtmlWithOrder() {
        webTestClient.get()
                .uri("/orders/"+orderId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.TEXT_HTML)
                .expectBody(String.class).consumeWith(response -> {
                    String body = response.getResponseBody();
                    assertNotNull(body);
                    assertTrue(body.contains("1.0 руб."));
                });
    }

}
