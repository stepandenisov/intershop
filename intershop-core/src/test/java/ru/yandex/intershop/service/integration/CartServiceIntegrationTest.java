package ru.yandex.intershop.service.integration;


import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.User;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.repository.CartRepository;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ItemService;

import java.util.ArrayList;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;


public class CartServiceIntegrationTest extends BaseServiceIntegrationTest {


    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ItemService itemService;

    @Test
    void modifyItemCountByItemId_and_getItemsFromCart__shouldModifyItemCountInCart_and_shouldReturnItemsFromCart() {
        User user = userRepository.save(new User(null, "admin", "password", "ADMIN")).block();
        Long userId = user.getId();
        Cart cart = new Cart(null, 0.0, userId, new ArrayList<>());
        cartRepository.save(cart).block();
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});
        itemService.saveItemWithImage(item, image)
                .flatMap(savedItemId -> cartService.modifyItemCountByItemId(userId, savedItemId, Action.PLUS))
                .block();

        cart = cartService.findCartWithCartItemsByUserId(userId).block();
        assertNotNull(cart);
        assertEquals(1, cart.getCartItems().size(), "Корзина должна быть с одним товаром");
        assertEquals(1, cart.getCartItems().get(0).getItemCount(), "Количество должно быть равно 1");
        assertNotNull(cart.getCartItems().get(0).getItem(), "Товар не должен быть null");
    }

    @Test
    void removeItemsFromCart_shouldRemoveItemsFromCart() {
        User user = userRepository.save(new User(null, "admin", "password", "ADMIN")).block();
        Long userId = user.getId();
        Cart cart = new Cart(null, 0.0, userId, new ArrayList<>());
        cartRepository.save(cart).block();
        Item item = new Item(null, "title", "description", 12.0);
        Image image = new Image(null, null, new byte[]{1});
        itemService.saveItemWithImage(item, image)
                .flatMap(savedItemId -> cartService.modifyItemCountByItemId(userId, savedItemId, Action.MINUS))
                .then(cartService.removeItemsFromCartByUserId(userId))
                .block();
        cart = cartService.findCartWithCartItemsByUserId(userId).block();
        assertNotNull(cart);
        assertEquals(0, cart.getCartItems().size(), "Корзина должна быть пуста");
    }
}
