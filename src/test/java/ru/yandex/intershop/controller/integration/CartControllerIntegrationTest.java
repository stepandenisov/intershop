package ru.yandex.intershop.controller.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.item.Item;

import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.xpath;

public class CartControllerIntegrationTest extends BaseControllerIntegrationTest{


    @BeforeEach
    void customSetUp() {
        Cart cart = new Cart(null, 1.0, null);
        Item item = new Item(null, "title", "description", 1.0);
        Item savedItem = itemRepository.save(item);
        CartItem cartItem = new CartItem(null, cart, savedItem, 1);
        cart.setCartItems(List.of(cartItem));
        cartRepository.save(cart);
    }


    @Test
    void cart_shouldReturnHtmlWithCart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(xpath("//table/tr").nodeCount(2))
                .andExpect(xpath("//table/tr[1]/td/b").string("Итого: 0.0 руб."));
    }

    @Test
    void modifyItemCount_shouldModifyCountAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items/1?action=PLUS&redirectUrl=/cart/items/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items/"));
    }

    @Test
    void buy_shouldBuyItemsFromCartAndRedirect() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/cart/items/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/orders/1?newOrder=true"));
    }

}
