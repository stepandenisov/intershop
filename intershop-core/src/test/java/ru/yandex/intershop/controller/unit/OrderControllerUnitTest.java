package ru.yandex.intershop.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.controller.order.OrderController;
import ru.yandex.intershop.model.User;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.service.OrderService;
import ru.yandex.intershop.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(OrderController.class)
public class OrderControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private UserService userService;


    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void order_shouldReturnHtmlWithOrder(){

        Order order = new Order(1L, 1.0, 1L, List.of());

        User admin = new User(1L, "admin", "password", "ROLE_ADMIN");

        when(userService.findUserByName("admin")).thenReturn(Mono.just(admin));
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

    @WithAnonymousUser
    void order_shouldRedirect() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/orders/1")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void orders_shouldReturnHtmlWithOrders(){

        Order order = new Order(1L, 1.0, 1L, List.of());
        User admin = new User(1L, "admin", "password", "ROLE_ADMIN");

        when(userService.findUserByName("admin")).thenReturn(Mono.just(admin));
        when(orderService.findAllByUserId(1L)).thenReturn(Flux.fromIterable(List.of(order)));

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

    @WithAnonymousUser
    void orders_shouldRedirect() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

}
