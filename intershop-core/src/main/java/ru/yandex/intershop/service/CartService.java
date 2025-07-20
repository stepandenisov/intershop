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

    private Mono<Cart> createEmptyCart() {
        Cart cart = new Cart(null, 0.0, new ArrayList<>());
        return cartRepository.save(cart);
    }


    public Mono<Cart> findCartWithCartItemsById(Long id) {
        return cartRepository.findById(id)
                .flatMap(cart ->
                        Mono.just(cart)
                                .zipWith(cartItemRepository.findAllByCartId(cart.getId())
                                        .zipWith(itemService.findAll())
                                        .map(tuple -> tuple.getT1().withItem(tuple.getT2())).collectList())
                                .map(tuple -> tuple.getT1().withCartItems(tuple.getT2()))
                );
    }

    public Mono<Void> modifyItemCountByItemId(Long itemId, Action action) {
        return findCartWithCartItemsById(1L)
                .switchIfEmpty(createEmptyCart())
                .flatMap(cart -> Flux.fromIterable(cart.getCartItems())
                        .switchIfEmpty(Flux.defer(() -> itemService.findItemById(itemId)
                                .flatMap(item -> Mono.just(new CartItem(null, cart.getId(), item.getId(), 0, item)))
                                .flatMap(cartItemRepository::save)
                                .doOnNext(cart.getCartItems()::add)
                        ))
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
                                    if (cartItem.getItemCount() == 0){
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
                ).then(updateTotalOfCartById(1L))
                .then(itemService.flushCacheById(itemId))
                .then(itemService.flushListCaches());
    }

    public Mono<Void> updateTotalOfCartById(Long cartId) {
        Mono<Cart> cartMono = findCartWithCartItemsById(cartId);
        return cartMono.flatMap(cart -> {
            Double total = cart.getCartItems().stream()
                    .map(cartItem -> cartItem.getItem().getPrice() * cartItem.getItemCount())
                    .reduce(Double::sum)
                    .orElse(0.0);
            cart.setTotal(total);
            return cartRepository.save(cart).then();
        });
    }

    public Mono<Void> removeItemsFromCart() {
        return cartItemRepository.deleteAllByCartId(1L)
                .then(updateTotalOfCartById(1L));
    }

}
