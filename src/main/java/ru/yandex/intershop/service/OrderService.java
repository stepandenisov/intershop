package ru.yandex.intershop.service;


import org.springframework.stereotype.Service;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.repository.OrderRepository;

import java.util.List;
import java.util.Optional;


@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService){
        this.orderRepository = orderRepository;
        this.cartService = cartService;
    }

    public Order save(Order order){
        Order createdOrder = orderRepository.save(order);
        cartService.removeItemsFromCart();
        return createdOrder;
    }

    public Optional<Order> findOrderById(Long id){
        return orderRepository.findById(id);
    }

    public List<Order> findAll(){
        return orderRepository.findAll();
    }

}
