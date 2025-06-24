package ru.yandex.intershop.controller.integration;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.repository.OrderRepository;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@DirtiesContext
public class OrderControllerIntegrationTest extends BaseControllerIntegrationTest{

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ItemRepository itemRepository;

    @BeforeEach
    void customSetUp(){
        Order order = new Order(null, 1.0, null);
        Item item = new Item(null, "title", "description", 1.0);
        Item savedItem = itemRepository.save(item);
        OrderItem orderItem = new OrderItem(null, order, savedItem, 1, 1.0);
        order.setOrderItems(List.of(orderItem));
        orderRepository.save(order);
    }

//    @Test
//    void order_shouldReturnHtmlWithOrder() throws Exception {
//        mockMvc.perform(MockMvcRequestBuilders.get("/orders/2"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType("text/html;charset=UTF-8"))
//                .andExpect(view().name("order"))
//                .andExpect(model().attributeExists("order"))
//                .andExpect(xpath("//table/tr").nodeCount(5))
//                .andExpect(xpath("//table/tr[3]/td/h3").string("Сумма: 1.0 руб."));
//    }

    @Test
    void orders_shouldReturnHtmlWithOrders() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(xpath("//table/tr/td").nodeCount(2))
                .andExpect(xpath("//table/tr/td[1]/p/b").string("Сумма: 1.0 руб."));
    }

}
