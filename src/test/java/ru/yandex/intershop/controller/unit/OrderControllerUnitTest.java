package ru.yandex.intershop.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.controller.item.ItemController;
import ru.yandex.intershop.controller.order.OrderController;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.service.ItemService;
import ru.yandex.intershop.service.OrderService;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

@WebMvcTest(OrderController.class)
public class OrderControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;


    @Test
    void order_shouldReturnHtmlWithOrder() throws Exception {

        Order order = new Order(1L, 1.0, List.of());

        when(orderService.findOrderById(1L)).thenReturn(Optional.of(order));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("order"))
                .andExpect(xpath("//table/tr").nodeCount(2))
                .andExpect(xpath("//table/tr[2]/td/h3").string("Сумма: 1.0 руб."));
    }

    @Test
    void orders_shouldReturnHtmlWithOrders() throws Exception {

        Order order = new Order(1L, 1.0, List.of());

        when(orderService.findAll()).thenReturn(List.of(order));

        mockMvc.perform(MockMvcRequestBuilders.get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"))
                .andExpect(xpath("//table/tr/td").nodeCount(1))
                .andExpect(xpath("//table/tr/td[1]/p/b").string("Сумма: 1.0 руб."));
    }

}
