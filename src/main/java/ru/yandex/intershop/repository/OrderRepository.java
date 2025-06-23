package ru.yandex.intershop.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.intershop.model.order.Order;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

}
