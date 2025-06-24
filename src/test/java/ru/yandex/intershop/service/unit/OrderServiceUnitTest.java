package ru.yandex.intershop.service.unit;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.repository.OrderRepository;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ItemService;
import ru.yandex.intershop.service.OrderService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class OrderServiceUnitTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartService cartService;

    @Test
    void save_shouldSaveAndReturnOrder(){
        Item item = new Item(1L, "title", "description", 1.0);
        Order order = new Order(1L, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, order, item, 1, item.getPrice())
        );
        order.setOrderItems(orderItems);

        doReturn(order).when(orderRepository).save(order);
        doNothing().when(cartService).removeItemsFromCart();
        Order savedOrder = orderService.save(order);

        assertNotNull(savedOrder, "Заказ должен быть");
        assertEquals(1.0, savedOrder.getTotal(), "Общая сумма должна быть 1.0");
    }

    @Test
    void findOrderById_shouldReturnOrder(){
        Item item = new Item(1L, "title", "description", 1.0);
        Order order = new Order(1L, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, order, item, 1, item.getPrice())
        );
        order.setOrderItems(orderItems);

        doReturn(Optional.of(order)).when(orderRepository).findById(1L);

        Optional<Order> actualOrder = orderService.findOrderById(1L);

        assertTrue(actualOrder.isPresent(), "Заказ должен быть");
        assertEquals(1.0, actualOrder.get().getTotal(), "Общая сумма должна быть 1.0");
    }

    @Test
    void findAll_shouldReturnOrders(){
        Item item = new Item(1L, "title", "description", 1.0);
        Order order = new Order(1L, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(1L, order, item, 1, item.getPrice())
        );
        order.setOrderItems(orderItems);

        doReturn(List.of(order)).when(orderRepository).findAll();

        List<Order> orders = orderService.findAll();

        assertEquals(1, orders.size(), "Количество заказов должно быть 1");
        assertEquals(1.0, orders.get(0).getTotal(), "Общая сумма должна быть 1.0");
    }

}
