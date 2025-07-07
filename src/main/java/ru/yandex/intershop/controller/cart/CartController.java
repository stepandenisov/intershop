package ru.yandex.intershop.controller.cart;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.*;

import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.OrderService;

@Controller
@RequestMapping("/cart/items")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @PostMapping("/{id}")
    public Mono<String> modifyItemCount(@PathVariable Long id,
                                  @RequestParam Action action,
                                  @RequestParam String redirectUrl) {
        return cartService.modifyItemCountByItemId(id, action)
                .then(Mono.just("redirect:" + redirectUrl));
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return cartService.findCartWithCartItemsById(1L)
                .flatMap(orderService::createOrderByCart)
                .flatMap(order -> Mono.just("redirect:/orders/" + order.getId() + "?newOrder=true"));
    }

    @GetMapping
    public Mono<String> cart(Model model) {
        return cartService.findCartWithCartItemsById(1L)
                .flatMap(cart -> {
                    model.addAttribute("items", cart.getCartItems());
                    model.addAttribute("total", cart.getTotal());
                    model.addAttribute("empty", cart.getCartItems().isEmpty());
                    return Mono.just("cart");
                });
    }

}
