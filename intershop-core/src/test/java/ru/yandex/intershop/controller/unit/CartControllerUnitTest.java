package ru.yandex.intershop.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.controller.cart.CartController;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.User;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.OrderService;
import ru.yandex.intershop.service.PaymentService;
import ru.yandex.intershop.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

@WebFluxTest(CartController.class)
public class CartControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void cart_shouldReturnHtmlWithCart() {

        Cart cart = new Cart(1L, 1.0, 1L, null);
        Item item = new Item(1L, "title", "description", 1.0);
        CartItem cartItem = new CartItem(1L, cart.getId(), item.getId(), 1, item);
        cart.setCartItems(List.of(cartItem));
        User admin = new User(1L, "admin", "password", "ROLE_ADMIN");

        when(userService.findUserByName("admin")).thenReturn(Mono.just(admin));
        when(cartService.findCartWithCartItemsByUserId(1L))
                .thenReturn(Mono.just(cart));

        when(paymentService.isBalanceEnough(1L, 1.0F)).thenReturn(Mono.just(true));

        webTestClient.get()
                .uri("/cart/items")
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
    @WithAnonymousUser
    void cart_shouldRedirect() {
        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void modifyItemCount_shouldModifyCountAndRedirect() {
        when(cartService.modifyItemCountByItemId(1L, 1L, Action.PLUS)).thenReturn(Mono.empty());
        var builder = new MultipartBodyBuilder();
        builder.part("action", "PLUS");
        User admin = new User(1L, "admin", "password", "ROLE_ADMIN");

        when(userService.findUserByName("admin")).thenReturn(Mono.just(admin));
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/cart/items/1?redirectUrl=/cart/items/")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(builder.build()))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/cart/items/");
    }

    @Test
    @WithAnonymousUser
    void modifyItemCount_shouldRedirect() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/cart/items/1?redirectUrl=/cart/items/")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void buy_shouldBuyItemsFromCartAndRedirect() {

        Cart cart = new Cart(1L, 1.0, 1L, null);
        Item item = new Item(1L, "title", "description", 1.0);
        CartItem cartItem = new CartItem(1L, cart.getId(), item.getId(), 1, item);
        cart.setCartItems(List.of(cartItem));
        User admin = new User(1L, "admin", "password", "ROLE_ADMIN");
        when(userService.findUserByName("admin")).thenReturn(Mono.just(admin));
        when(cartService.findCartWithCartItemsByUserId(1L))
                .thenReturn(Mono.just(cart));

        Order order = new Order(null, 1.0, 1L, null);
        List<OrderItem> orderItems = List.of(new OrderItem(1L, order.getId(), cartItem.getItem().getId(), cartItem.getItemCount(), cartItem.getItem().getPrice(), cartItem.getItem()));
        order.setOrderItems(orderItems);

        Order orderToReturn = new Order(1L, 1.0, 1L, orderItems);
        when(orderService.createOrderByCart(cart)).thenReturn(Mono.just(orderToReturn));

        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/cart/items/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().valueEquals(HttpHeaders.LOCATION, "/orders/1?newOrder=true");
    }

    @Test
    @WithAnonymousUser
    void buy_shouldRedirect() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/cart/items/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }
}
