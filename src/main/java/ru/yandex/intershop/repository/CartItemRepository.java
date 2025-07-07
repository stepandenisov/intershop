package ru.yandex.intershop.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.cart.CartItem;

@Repository
public interface CartItemRepository extends ReactiveCrudRepository<CartItem, Long> {

    Flux<CartItem> findAllByCartId(Long cartId);

    @Query(value = """
            SELECT sum(items.price) from carts_items
            LEFT outer join items on carts_items.item_id = items.id
            WHERE carts_items.cart_id = :cartId
            """)
    Mono<Double> getTotalPriceByCartId(@Param("cartId") Long cartId);

    Mono<Void> deleteAllByCartId(Long cartId);
}
