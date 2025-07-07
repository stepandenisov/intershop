package ru.yandex.intershop.service;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.model.order.OrderItem;
import ru.yandex.intershop.repository.OrderItemRepository;
import ru.yandex.intershop.repository.OrderRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;

    public OrderService(OrderRepository orderRepository, CartService cartService, OrderItemRepository orderItemRepository){
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.orderItemRepository = orderItemRepository;
    }

    public Mono<Order> createOrderByCart(Cart cart){
        return orderRepository.save(new Order(null, cart.getTotal(), new ArrayList<>()))
                .flatMap(savedOrder -> {
                    List<OrderItem> orderItems = cart.getCartItems().stream()
                            .map(cartItem -> new OrderItem(null,
                                    savedOrder.getId(),
                                    cartItem.getItemId(),
                                    cartItem.getItemCount())).toList();
                    return orderItemRepository.saveAll(orderItems)
                            .then(cartService.removeItemsFromCart())
                            .then(Mono.just(savedOrder));
                });
    }

    public Mono<Order> findOrderById(Long id){
        return orderRepository.findById(id)
                .flatMap(order ->
                        Mono.just(order)
                                .zipWith(orderItemRepository.findAllByOrderId(order.getId()).collectList())
                                .map(tupla -> tupla.getT1().withOrderItems(tupla.getT2()))
                );
    }

    public Flux<Order> findAll(){
        return orderRepository.findAll()
                .flatMap(orders ->
                        Mono.just(orders)
                                  .zipWith(orderItemRepository.findAllByOrderId(orders.getId()).collectList())
                                .map(tupla -> tupla.getT1().withOrderItems(tupla.getT2()))
                );
    }

}
