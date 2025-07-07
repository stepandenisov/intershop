package ru.yandex.intershop.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.Action;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.cart.CartItem;
import ru.yandex.intershop.repository.CartItemRepository;
import ru.yandex.intershop.repository.CartRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CartService {

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;
    private final ItemService itemService;

    public CartService(CartRepository cartRepository,
                       ItemService itemService,
                       CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.itemService = itemService;
    }

    private Mono<Cart> createEmptyCart() {
        Cart cart = new Cart(null, 0.0, new ArrayList<>());
        return cartRepository.save(cart);
    }

    public Mono<Cart> findCartWithCartItemsById(Long id){
        return cartRepository.findById(id)
                .flatMap(cart ->
                        Mono.just(cart)
                                .zipWith(cartItemRepository.findAllByCartId(cart.getId()).collectList())
                                .map(tupla -> tupla.getT1().withCartItems(tupla.getT2()))
                );
    }

    public Mono<Void> modifyItemCountByItemId(Long itemId, Action action) {
        return findCartWithCartItemsById(1L).flatMap(cart -> {
                    if (cart == null) {
                        return createEmptyCart();
                    } else {
                        return Mono.just(cart);
                    }
                })
                .flatMap(cart -> Flux.fromIterable(cart.getCartItems())
                        .filter(cartItem -> Objects.equals(cartItem.getItemId(), itemId))
                        .single()
                        .flatMap(cartItem -> switch (action) {
                            case PLUS -> {
                                cartItem.setItemCount(cartItem.getItemCount() + 1);
                                yield cartItemRepository.save(cartItem);
                            }
                            case MINUS -> {
                                if (cartItem.getItemCount() > 0) {
                                    cartItem.setItemCount(cartItem.getItemCount() - 1);
                                    yield cartItemRepository.save(cartItem);
                                }
                                else {
                                    cart.getCartItems().remove(cartItem);
                                    yield cartItemRepository.delete(cartItem);
                                }
                            }
                            case DELETE -> {
                                cart.getCartItems().remove(cartItem);
                                yield cartItemRepository.delete(cartItem);
                            }
                        })
                        .switchIfEmpty(Mono.defer(() -> {
                                            if (action == Action.PLUS) {
                                                return itemService.findItemById(itemId)
                                                        .flatMap(item -> {
                                                            if (item != null) {
                                                                return Mono.just(new CartItem(null, cart.getId(), item.getId(), 1));
                                                            }
                                                            return Mono.empty();
                                                        })
                                                        .flatMap(cartItemRepository::save)
                                                        .doOnNext(cart.getCartItems()::add);
                                            }
                                            return Mono.empty();
                                        })))
                .then(updateTotalOfCartById(1L));
    }

    private Mono<Void> updateTotalOfCartById(Long cartId){
        return findCartWithCartItemsById(cartId)
                .flatMap(cart -> cartItemRepository.getTotalPriceByCartId(cart.getId())
                        .doOnNext(cart::setTotal))
                .then();
    }

    public Mono<Void> removeItemsFromCart() {
        return cartItemRepository.deleteAllByCartId(1L);
    }

}
