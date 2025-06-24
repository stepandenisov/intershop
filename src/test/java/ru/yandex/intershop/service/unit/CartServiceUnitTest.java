package ru.yandex.intershop.service.unit;


import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.repository.CartRepository;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ItemService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CartServiceUnitTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;


    @Test
    void modifyItemCountByItemId_and_getItemsFromCart__shouldModifyItemCountInCart_and_shouldReturnItemsFromCart() {

        Item item = new Item(1L, "title", "description", 12.0);
        Cart cart = new Cart(1L, 1.0, null);
        List<CartItem> cartItems = List.of(
                new CartItem(1L, cart, item, 1)
        );
        cart.setCartItems(cartItems);
        doReturn(Optional.of(cart)).when(cartRepository).findById(1L);

        cartService.modifyItemCountByItemId(1L, Action.PLUS);

        doReturn(cart).when(cartRepository).save(cart);

        cartService.modifyItemCountByItemId(1L, Action.PLUS);
    }

    @Test
    void getItemsFromCart_shouldReturnItems() {

        Item item = new Item(1L, "title", "description", 12.0);
        Cart cart = new Cart(1L, 1.0, null);
        List<CartItem> cartItems = List.of(
                new CartItem(1L, cart, item, 1)
        );
        cart.setCartItems(cartItems);
        doReturn(Optional.of(cart)).when(cartRepository).findById(1L);

        List<CartItem> itemsFromCart = cartService.getItemsFromCart();

        assertEquals(cartItems.size(), itemsFromCart.size(), "Размеры должны совпадать");
        assertEquals(cartItems.get(0), itemsFromCart.get(0), "Товары должны совпадать");
    }

    @Test
    void removeItemsFromCart_shouldRemoveItemsFromCart() {

        Item item = new Item(1L, "title", "description", 12.0);
        Cart cart = new Cart(1L, 1.0, null);
        List<CartItem> cartItems = new ArrayList<>(List.of(
                new CartItem(1L, cart, item, 1)
        ));
        cart.setCartItems(cartItems);
        doReturn(Optional.of(cart)).when(cartRepository).findById(1L);

        doReturn(cart).when(cartRepository).save(cart);

        cartService.removeItemsFromCart();
    }

}
