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


@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderItemRepository orderItemRepository;
    private final CartService cartService;

    private final ItemService itemService;

    private final PaymentService paymentService;

    public OrderService(OrderRepository orderRepository,
                        CartService cartService,
                        OrderItemRepository orderItemRepository,
                        ItemService itemService,
                        PaymentService paymentService){
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.itemService = itemService;
        this.orderItemRepository = orderItemRepository;
        this.paymentService = paymentService;
    }

    public Mono<Order> createOrderByCart(Cart cart){
        return paymentService.buy().then(orderRepository.save(new Order(null, cart.getTotal(), new ArrayList<>()))
                .flatMap(savedOrder -> {
                    List<OrderItem> orderItems = cart.getCartItems().stream()
                            .map(cartItem -> new OrderItem(null,
                                    savedOrder.getId(),
                                    cartItem.getItemId(),
                                    cartItem.getItemCount(),
                                    cartItem.getItem().getPrice(),
                                    cartItem.getItem())).toList();
                    return orderItemRepository.saveAll(orderItems)
                            .then(cartService.removeItemsFromCart())
                            .then(Mono.just(savedOrder));
                }));
    }

    public Mono<Order> findOrderById(Long id){
        return orderRepository.findById(id)
                .flatMap(order ->
                        Mono.just(order)
                                .zipWith(orderItemRepository.findAllByOrderId(order.getId())
                                        .zipWith(itemService.findAll())
                                        .map(tuple -> tuple.getT1().withItem(tuple.getT2())).collectList())
                                .map(tuple -> tuple.getT1().withOrderItems(tuple.getT2()))
                );
    }

    public Flux<Order> findAll(){
        return orderRepository.findAll()
                .flatMap(orders ->
                        Mono.just(orders)
                                  .zipWith(orderItemRepository.findAllByOrderId(orders.getId()).zipWith(itemService.findAll())
                                          .map(tuple -> tuple.getT1().withItem(tuple.getT2())).collectList())
                                .map(tuple -> tuple.getT1().withOrderItems(tuple.getT2()))
                );
    }

}
