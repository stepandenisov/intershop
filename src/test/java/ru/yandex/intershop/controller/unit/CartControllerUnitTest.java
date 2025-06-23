package ru.yandex.intershop.controller.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.controller.cart.CartController;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.OrderService;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

@WebMvcTest(CartController.class)
public class CartControllerUnitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private OrderService orderService;

    @Test
    void cart_shouldReturnHtmlWithCart() throws Exception {

        Cart cart = new Cart(1L, 1.0, null);
        Item item = new Item(1L, "title", "description", 1.0);
        CartItem cartItem = new CartItem(1L, cart, item, 1);
        cart.setCartItems(List.of(cartItem));
        when(cartService.getItemsFromCart())
                .thenReturn(List.of(cartItem));

        mockMvc.perform(MockMvcRequestBuilders.get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(xpath("//table/tr").nodeCount(7));
    }

    @Test
    void modifyItemCount_shouldModifyCountAndRedirect() throws Exception {
        doNothing().when(cartService).modifyItemCountByItemId(1L, Action.PLUS);
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items/1?action=PLUS&redirectUrl=/cart/items/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items/"));
    }

    @Test
    void buy_shouldBuyItemsFromCartAndRedirect() throws Exception {

        Cart cart = new Cart(1L, 1.0, null);
        Item item = new Item(1L, "title", "description", 1.0);
        CartItem cartItem = new CartItem(1L, cart, item, 1);
        cart.setCartItems(List.of(cartItem));
        when(cartService.getItemsFromCart())
                .thenReturn(List.of(cartItem));

        Order order = new Order(null, 1.0, null);
        List<OrderItem> orderItems = List.of(new OrderItem(null, order, cartItem.getItem(), cartItem.getItemCount(), cartItem.getItem().getPrice()));
        order.setOrderItems(orderItems);

        Order orderToReturn = new Order(1L, 1.0, orderItems);
        when(orderService.save(order)).thenReturn(orderToReturn);

        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1?newOrder=true"));
    }
}
