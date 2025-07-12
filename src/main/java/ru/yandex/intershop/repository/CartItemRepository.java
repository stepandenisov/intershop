package ru.yandex.intershop.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.cart.CartItem;

@Repository
public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {

    Flux<CartItem> findAllByCartId(Long cartId);

    Mono<Void> deleteAllByCartId(Long cartId);
}
