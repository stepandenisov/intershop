package ru.yandex.intershop.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.intershop.model.order.Order;

@Repository
public interface OrderRepository extends ReactiveCrudRepository<Order, Long> {

}
