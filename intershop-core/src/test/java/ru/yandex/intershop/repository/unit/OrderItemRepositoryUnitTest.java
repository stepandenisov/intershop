package ru.yandex.intershop.repository.unit;


import org.junit.jupiter.api.Test;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderItemRepositoryUnitTest extends BaseRepositoryUnitTest{


    @Test
    void findAllByOrderId_shouldReturnAllByOrderId(){
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item).block();
        Order order = new Order(null, 1.0, new ArrayList<>());
        Order savedOrder = orderRepository.save(order).block();
        OrderItem orderItem = new OrderItem(null, order.getId(), item.getId(), 1, item.getPrice(), item);
        orderItemRepository.save(orderItem).block();

        List<OrderItem> orderItems = orderItemRepository.findAllByOrderId(1L).collectList().block();

        assertNotNull(orderItems);

        assertEquals(1, orderItems.size(), "Количество товаров должно быть равно 1");
        assertEquals(item.getId(), orderItems.get(0).getItemId(), "Описание должно быть description");
    }
}
