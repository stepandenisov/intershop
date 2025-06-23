package ru.yandex.intershop.service;

import org.springframework.stereotype.Service;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final ItemService itemService;

    public CartService(CartRepository cartRepository, ItemService itemService) {
        this.cartRepository = cartRepository;
        this.itemService = itemService;
    }

    private Cart createEmptyCart() {
        Cart cart = new Cart(null, 0.0, new ArrayList<>());
        return cartRepository.save(cart);
    }

    public void modifyItemCountByItemId(Long itemId, Action action) {
        Cart cart = cartRepository.findById(1L)
                .orElse(createEmptyCart());
        cart.getCartItems().stream()
                .filter(cartItem -> Objects.equals(cartItem.getItem().getId(), itemId))
                .findFirst()
                .ifPresentOrElse(cartItem -> {
                            switch (action) {
                                case PLUS -> cartItem.setItemCount(cartItem.getItemCount() + 1);
                                case MINUS -> {
                                    if (cartItem.getItemCount() > 0) {
                                        cartItem.setItemCount(cartItem.getItemCount() - 1);
                                    }
                                    if (cartItem.getItemCount() == 0){
                                        cart.getCartItems().remove(cartItem);
                                    }
                                }
                                case DELETE -> cart.getCartItems().remove(cartItem);
                            }
                            cartRepository.save(cart);
                        },
                        () -> {
                            if (action == Action.PLUS) {
                                itemService.findItemById(itemId)
                                        .ifPresent(item -> {
                                            CartItem cartItem = new CartItem(null, cart, item, 1);
                                            cart.getCartItems().add(cartItem);
                                            cartRepository.save(cart);
                                        });
                            }
                        });
    }

    public List<CartItem> getItemsFromCart() {
        return cartRepository.findById(1L)
                .map(Cart::getCartItems)
                .orElse(new ArrayList<>());
    }

    public void removeItemsFromCart() {
        cartRepository.findById(1L)
                .ifPresent(cart -> {
                    while (!cart.getCartItems().isEmpty()){
                        cart.getCartItems().remove(0);
                    }
                    cartRepository.save(cart);
                });
    }

}
