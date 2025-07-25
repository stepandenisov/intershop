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


    public Mono<Cart> findCartWithCartItemsByUserId(Long userId) {
        return cartRepository.findByUserId(userId)
                .flatMap(cart ->
                        Mono.just(cart)
                                .zipWith(cartItemRepository.findAllByCartId(cart.getId())
                                        .zipWith(itemService.findAll())
                                        .map(tuple -> tuple.getT1().withItem(tuple.getT2())).collectList())
                                .map(tuple -> tuple.getT1().withCartItems(tuple.getT2()))
                );
    }

    public Mono<Void> modifyItemCountByItemId(Long userId, Long itemId, Action action) {
        return findCartWithCartItemsByUserId(userId)
                .flatMap(cart -> Flux.fromIterable(cart.getCartItems())
                        .switchIfEmpty(Flux.defer(() -> itemService.findItemById(itemId)
                                .flatMap(item -> Mono.just(new CartItem(null, cart.getId(), item.getId(), 0, item)))
                                .flatMap(cartItemRepository::save)
                                .doOnNext(cart.getCartItems()::add)
                        ))
                        .filter(cartItem -> Objects.equals(cartItem.getItemId(), itemId))
                        .switchIfEmpty(Flux.defer(() -> itemService.findItemById(itemId)
                                .flatMap(item -> Mono.just(new CartItem(null, cart.getId(), item.getId(), 0, item)))
                                .flatMap(cartItemRepository::save)
                                .doOnNext(cart.getCartItems()::add)
                        ))
                        .single()
                        .flatMap(cartItem -> switch (action) {
                            case PLUS -> {
                                cartItem.setItemCount(cartItem.getItemCount() + 1);
                                yield cartItemRepository.save(cartItem);
                            }
                            case MINUS -> {
                                if (cartItem.getItemCount() > 0) {
                                    cartItem.setItemCount(cartItem.getItemCount() - 1);
                                    if (cartItem.getItemCount() == 0) {
                                        yield cartItemRepository.delete(cartItem);
                                    } else {
                                        yield cartItemRepository.save(cartItem);
                                    }
                                } else {
                                    cart.getCartItems().remove(cartItem);
                                    yield cartItemRepository.delete(cartItem);
                                }
                            }
                            case DELETE -> {
                                cart.getCartItems().remove(cartItem);
                                yield cartItemRepository.delete(cartItem);
                            }
                        })
                ).then(updateTotalOfCartByUserId(userId))
                .then(itemService.flushCacheByUserId(userId))
                .then(itemService.flushListCachesByUserId(userId));
    }

    public Mono<Void> updateTotalOfCartByUserId(Long userId) {
        return findCartWithCartItemsByUserId(userId)
                .flatMap(cart -> {
                    Double total = cart.getCartItems().stream()
                            .map(cartItem -> cartItem.getItem().getPrice() * cartItem.getItemCount())
                            .reduce(Double::sum)
                            .orElse(0.0);
                    cart.setTotal(total);
                    return cartRepository.save(cart).then();
                });
    }

    public Mono<Void> removeItemsFromCartByUserId(Long userId) {
        return findCartWithCartItemsByUserId(userId)
                .flatMap(cart -> cartItemRepository.deleteAllByCartId(cart.getId()).thenReturn(cart.getId())
                )
                .flatMap(this::updateTotalOfCartByUserId);
    }

}
