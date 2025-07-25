package ru.yandex.intershop.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.cart.Cart;


@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, Long>{

    Mono<Cart> findByUserId(Long userId);
}
