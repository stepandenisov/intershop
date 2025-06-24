package ru.yandex.intershop.service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.service.OrderService;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class OrderServiceIntegrationTest extends BaseServiceIntegrationTest{

    @Autowired
    private OrderService orderService;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    void save_shouldSaveAndReturnOrder(){
        Item item = new Item(null, "title", "description", 1.0);
        itemRepository.save(item);
        Order order = new Order(null, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, order, item, 1, item.getPrice())
        );
        order.setOrderItems(orderItems);

        Order savedOrder = orderService.save(order);

        assertNotNull(savedOrder, "Заказ должен быть");
        assertEquals(1.0, savedOrder.getTotal(), "Общая сумма должна быть 1.0");
    }

    @Test
    void findOrderById_shouldReturnOrder(){
        Item item = new Item(null, "title", "description", 1.0);
        itemRepository.save(item);
        Order order = new Order(null, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, order, item, 1, item.getPrice())
        );
        order.setOrderItems(orderItems);

        Order savedOrder = orderService.save(order);

        Optional<Order> actualOrder = orderService.findOrderById(savedOrder.getId());

        assertTrue(actualOrder.isPresent(), "Заказ должен быть");
        assertEquals(1.0, actualOrder.get().getTotal(), "Общая сумма должна быть 1.0");
    }

    @Test
    void findAll_shouldReturnOrders(){
        Item item = new Item(null, "title", "description", 1.0);
        itemRepository.save(item);
        Order order = new Order(null, 1.0, null);
        List<OrderItem> orderItems = List.of(
                new OrderItem(null, order, item, 1, item.getPrice())
        );
        order.setOrderItems(orderItems);

        orderService.save(order);

        List<Order> orders = orderService.findAll();

        assertEquals(1, orders.size(), "Количество заказов должно быть 1");
        assertEquals(1.0, orders.get(0).getTotal(), "Общая сумма должна быть 1.0");
    }
}
