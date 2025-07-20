package ru.yandex.intershop.service.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.repository.OrderItemRepository;
import ru.yandex.intershop.repository.OrderRepository;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ItemService;
import ru.yandex.intershop.service.OrderService;
import ru.yandex.intershop.service.PaymentService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderServiceUnitTest {

    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private ItemService itemService;
    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private PaymentService paymentService;

    @Test
    void save_shouldSaveAndReturnOrder() {
        Item item = new Item(1L, "title", "description", 1.0);
        Order order = new Order(1L, 1.0, null);
        Cart cart = new Cart(1L, 1.0, new ArrayList<>());
        List<CartItem> cartItems = List.of(
                new CartItem(1L, cart.getId(), item.getId(), 1, item)
        );
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, order.getId(), item.getId(), 1, item.getPrice(), item)
        );
        cart.setCartItems(cartItems);

        when(orderRepository.save(any(Order.class))).thenReturn(Mono.just(order));
        when(cartService.removeItemsFromCart()).thenReturn(Mono.empty());
        when(orderItemRepository.saveAll(anyIterable())).thenReturn(Flux.fromIterable(orderItems));
        when(paymentService.buy(1.0F)).thenReturn(Mono.just(true));
        Order savedOrder = orderService.createOrderByCart(cart).block();

        assertNotNull(savedOrder, "Заказ должен быть");
        assertEquals(1.0, savedOrder.getTotal(), "Общая сумма должна быть 1.0");
    }

    @Test
    void findOrderById_shouldReturnOrder() {
        Item item = new Item(1L, "title", "description", 1.0);
        Order order = new Order(1L, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, order.getId(), item.getId(), 1, item.getPrice(), item)
        );
        order.setOrderItems(orderItems);

        when(orderRepository.findById(1L)).thenReturn(Mono.just(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Flux.fromIterable(orderItems));
        when(itemService.findAll()).thenReturn(Flux.just(item));

        Optional<Order> actualOrder = orderService.findOrderById(1L).blockOptional();

        assertTrue(actualOrder.isPresent(), "Заказ должен быть");
        assertEquals(1.0, actualOrder.get().getTotal(), "Общая сумма должна быть 1.0");
    }

    @Test
    void findAll_shouldReturnOrders() {
        Item item = new Item(1L, "title", "description", 1.0);
        Order order = new Order(1L, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, order.getId(), item.getId(), 1, item.getPrice(), item)
        );
        order.setOrderItems(orderItems);

        when(orderRepository.findAll()).thenReturn(Flux.just(order));
        when(orderItemRepository.findAllByOrderId(1L)).thenReturn(Flux.fromIterable(orderItems));
        when(itemService.findAll()).thenReturn(Flux.just(item));

        List<Order> orders = orderService.findAll().collectList().block();
        assertNotNull(orders);
        assertEquals(1, orders.size(), "Количество заказов должно быть 1");
        assertEquals(1.0, orders.get(0).getTotal(), "Общая сумма должна быть 1.0");
    }

}
