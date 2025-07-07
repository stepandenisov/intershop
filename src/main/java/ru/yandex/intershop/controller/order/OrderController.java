package ru.yandex.intershop.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.order.Order;
import ru.yandex.intershop.service.OrderService;

@RequestMapping("/orders")
@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public Mono<String> order(@PathVariable Long id,
                              @RequestParam(required = false, defaultValue = "false") boolean newOrder,
                              Model model) {
        return orderService.findOrderById(id)
                .flatMap(order -> {
                    model.addAttribute("order", order);
                    model.addAttribute("newOrder", newOrder);
                    return Mono.just("order");
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just("redirect:/orders")));
    }

    @GetMapping()
    public Mono<String> orders(Model model) {
        Flux<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return Mono.just("orders");
    }
}
