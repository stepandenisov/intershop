package ru.yandex.intershop.repository.unit;

import org.junit.jupiter.api.Test;
import ru.yandex.intershop.model.User;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.item.Item;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CartItemRepositoryUnitTest extends BaseRepositoryUnitTest{


    @Test
    void findAllByCartId_shouldReturnAllByCartId() {
        User user = userRepository.save(new User(null, "user", "Password", "ROLE_ADMIN")).block();
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item).block();
        Cart cart = new Cart(null, 1.0, user.getId(), new ArrayList<>());
        Cart savedCart = cartRepository.save(cart).block();
        assertNotNull(savedCart);
        assertNotNull(savedItem);
        CartItem cartItem = new CartItem(null, savedCart.getId(), savedItem.getId(), 1, item);
        cartItemRepository.save(cartItem).block();

        List<CartItem> cartItems = cartItemRepository.findAllByCartId(savedCart.getId()).collectList().block();

        assertNotNull(cartItems);

        assertEquals(1, cartItems.size(), "Количество товаров должно быть равно 1");
        assertEquals(savedItem.getId(), cartItems.get(0).getItemId(), "ID д.б 1");
    }

    @Test
    void deleteAllByCartId_shouldDeleteAllByCartId() {
        User user = userRepository.save(new User(null, "user", "Password", "ROLE_ADMIN")).block();
        Item item = new Item(null, "title", "description", 12.0);
        Item savedItem = itemRepository.save(item).block();
        Cart cart = new Cart(null, 1.0, user.getId(), new ArrayList<>());
        Cart savedCart = cartRepository.save(cart).block();
        CartItem cartItem = new CartItem(null, cart.getId(), item.getId(), 1, item);
        cartItemRepository.save(cartItem).block();

        cartItemRepository.deleteAllByCartId(1L).block();
        List<CartItem> cartItems = cartItemRepository.findAllByCartId(1L).collectList().block();
        assertNotNull(cartItems);

        assertEquals(0, cartItems.size(), "Количество товаров должно быть равно 0");
    }
}
