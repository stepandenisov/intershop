package ru.yandex.intershop.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.controller.order.OrderController;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.service.OrderService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@WebFluxTest(OrderController.class)
public class OrderControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;


    @Test
    void order_shouldReturnHtmlWithOrder(){

        Order order = new Order(1L, 1.0, List.of());

        when(orderService.findOrderById(1L)).thenReturn(Mono.just(order));

        webTestClient.get()
                .uri("/orders/1")
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
    void orders_shouldReturnHtmlWithOrders(){

        Order order = new Order(1L, 1.0, List.of());

        when(orderService.findAll()).thenReturn(Flux.fromIterable(List.of(order)));

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

}
