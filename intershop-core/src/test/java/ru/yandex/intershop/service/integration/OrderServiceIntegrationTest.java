package ru.yandex.intershop.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.repository.CartItemRepository;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.repository.OrderRepository;
import ru.yandex.intershop.service.OrderService;
import ru.yandex.intershop.service.PaymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class OrderServiceIntegrationTest extends BaseServiceIntegrationTest{

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    private Long cartId;

    @Test
    void saveAndFindById_shouldSaveAndReturnOrder(){
        Mono<Item> itemMono = itemRepository.save(new Item(null, "title", "description", 1.0));
        Mono<Cart> cartMono = cartRepository.save(new Cart(null, 1.0, new ArrayList<>()));
        when(paymentService.buy(1.0F)).thenReturn(Mono.just(true));
        Mono.zip(itemMono, cartMono)
                .flatMap(tuple -> {
                    Cart cart = tuple.getT2();
                    cartId = cart.getId();
                    Item item = tuple.getT1();
                    CartItem cartItem = new CartItem(null, cart.getId(), item.getId(), 1, item);
                    return cartItemRepository.save(cartItem);
                }).block();
        Cart cart = cartRepository.findById(cartId).block();
        assertNotNull(cart, "Корзина должна быть");
        Order savedOrder = orderService.createOrderByCart(cart).block();
        assertNotNull(savedOrder, "Заказ должен быть");

        Optional<Order> actualOrder = orderService.findOrderById(savedOrder.getId()).blockOptional();

        assertTrue(actualOrder.isPresent(), "Заказ должен быть");
        assertEquals(1.0, actualOrder.get().getTotal(), "Общая сумма должна быть 1.0");
    }

    @Test
    void findAll_shouldReturnOrders(){
        Mono<Item> itemMono = itemRepository.save(new Item(null, "title", "description", 1.0));
        Mono<Cart> cartMono = cartRepository.save(new Cart(null, 1.0, new ArrayList<>()));
        when(paymentService.buy(1.0F)).thenReturn(Mono.just(true));
        Mono.zip(itemMono, cartMono)
                .flatMap(tuple -> {
                    Cart cart = tuple.getT2();
                    cartId = cart.getId();
                    Item item = tuple.getT1();
                    CartItem cartItem = new CartItem(null, cart.getId(), item.getId(), 1, item);
                    return cartItemRepository.save(cartItem);
                }).block();
        Cart cart = cartRepository.findById(cartId).block();
        assertNotNull(cart, "Корзина должна быть");
        Order savedOrder = orderService.createOrderByCart(cart).block();
        assertNotNull(savedOrder, "Заказ должен быть");

        List<Order> orders = orderService.findAll().collectList().block();

        assertNotNull(orders, "Заказ должен быть");

        assertEquals(1, orders.size(), "Количество заказов должно быть 1");
        assertEquals(1.0, orders.get(0).getTotal(), "Общая сумма должна быть 1.0");
    }
}
