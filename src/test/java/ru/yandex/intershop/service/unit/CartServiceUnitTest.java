package ru.yandex.intershop.service.unit;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.repository.CartItemRepository;
import ru.yandex.intershop.repository.CartRepository;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ItemService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class CartServiceUnitTest {

    @Autowired
    private CartService cartService;

    @MockitoBean
    private CartRepository cartRepository;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoBean
    private ItemService itemService;


    @Test
    void modifyItemCountByItemId_and_getItemsFromCart__shouldModifyItemCountInCart_and_shouldReturnItemsFromCart() {

        Item item = new Item(1L, "title", "description", 12.0);
        Cart cart = new Cart(1L, 1.0, null);
        List<CartItem> cartItems = List.of(
                new CartItem(1L, cart.getId(), item.getId(), 1, item)
        );
        cart.setCartItems(cartItems);
        when(cartRepository.findById(1L)).thenReturn(Mono.just(cart));
        when(cartItemRepository.findAllByCartId(1L)).thenReturn(Flux.fromIterable(cartItems));
        when(itemService.findAll()).thenReturn(Flux.fromIterable(List.of(item)));
        when(itemService.findItemById(1L)).thenReturn(Mono.just(item));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(Mono.just(cartItems.get(0)));
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(cart));
        cartService.modifyItemCountByItemId(1L, Action.PLUS).block();
    }

    @Test
    void getItemsFromCart_shouldReturnItems() {

        Item item = new Item(1L, "title", "description", 12.0);
        Cart cart = new Cart(1L, 1.0, null);
        List<CartItem> cartItems = List.of(
                new CartItem(1L, cart.getId(), item.getId(), 1, item)
        );
        cart.setCartItems(cartItems);
        when(cartRepository.findById(1L)).thenReturn(Mono.just(cart));
        when(cartItemRepository.findAllByCartId(1L)).thenReturn(Flux.fromIterable(cartItems));
        when(itemService.findAll()).thenReturn(Flux.fromIterable(List.of(item)));
        Cart cart1 = cartService.findCartWithCartItemsById(1L).block();

        assertNotNull(cart1);
        assertEquals(cartItems.size(), cart1.getCartItems().size(), "Размеры должны совпадать");
        assertEquals(cartItems.get(0), cart1.getCartItems().get(0), "Товары должны совпадать");
    }

    @Test
    void removeItemsFromCart_shouldRemoveItemsFromCart() {

        Item item = new Item(1L, "title", "description", 12.0);
        Cart cart = new Cart(1L, 1.0, null);
        List<CartItem> cartItems = List.of(
                new CartItem(1L, cart.getId(), item.getId(), 1, item)
        );
        cart.setCartItems(cartItems);
        when(cartRepository.findById(1L)).thenReturn(Mono.just(cart));
        when(cartItemRepository.deleteAllByCartId(1L)).thenReturn(Mono.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(Mono.just(cart));

        cartService.removeItemsFromCart().block();
    }

}
