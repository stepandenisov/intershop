package ru.yandex.intershop.service.integration;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CartServiceIntegrationTest extends BaseServiceIntegrationTest{


    @Autowired
    private CartService cartService;

    @Autowired
    private ItemService itemService;

    @Test
    void modifyItemCountByItemId_and_getItemsFromCart__shouldModifyItemCountInCart_and_shouldReturnItemsFromCart() {

        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});
        Item savedItem = itemService.saveItemWithImage(item, image);

        cartService.modifyItemCountByItemId(savedItem.getId(), Action.PLUS);

        List<CartItem> items = cartService.getItemsFromCart();
        assertEquals(1, items.size(), "Корзина должна быть с одним товаром");
        assertEquals(1, items.get(0).getItemCount(), "Количество должно быть равно 1");
        assertNotNull(items.get(0).getItem(), "Товар не должен быть null");
        assertEquals(1L, items.get(0).getItem().getId(), "id товара должен быть 1");
    }

    @Test
    void removeItemsFromCart_shouldRemoveItemsFromCart() {
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});
        Item savedItem = itemService.saveItemWithImage(item, image);

        cartService.modifyItemCountByItemId(savedItem.getId(), Action.PLUS);

        cartService.removeItemsFromCart();
        List<CartItem> items = cartService.getItemsFromCart();
        assertEquals(0, items.size(), "Корзина должна быть пуста");
    }
}
