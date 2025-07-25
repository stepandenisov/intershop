package ru.yandex.intershop.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.service.OrderService;
import ru.yandex.intershop.service.UserService;

@RequestMapping("/orders")
@Controller
public class OrderController {

    private final OrderService orderService;

    private final UserService userService;

    public OrderController(OrderService orderService, UserService userService) {
        this.orderService = orderService;
        this.userService = userService;
    }

    @GetMapping("/{id}")
    public Mono<String> order(@PathVariable("id") Long id,
                              @RequestParam(name = "newOrder", required = false, defaultValue = "false") boolean newOrder,
                              Model model, ServerWebExchange exchange) {
        Mono<Long> userIdMono = exchange.getPrincipal()
                .flatMap(principal -> userService.findUserByName(principal.getName()))
                        .flatMap(user -> Mono.just(user.getId()));

        Mono<Order> orderMono = orderService.findOrderById(id);
        return Mono.zip(userIdMono, orderMono)
                .filter(tuple -> {
                    Long userId = tuple.getT1();
                    Order order = tuple.getT2();
                    return userId.equals(order.getUserId());
                })
                .flatMap(tuple -> {
                    Order order = tuple.getT2();
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                    return Mono.just("order");
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just("redirect:/orders")));
    }

    @GetMapping()
    public Mono<String> orders(Model model, ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .flatMap(principal -> userService.findUserByName(principal.getName()))
                .flatMapMany(user -> orderService.findAllByUserId(user.getId()))
                .collectList()
                .flatMap(orders -> {
                    model.addAttribute("orders", orders);
                    return Mono.just("orders");
                });

    }
}
