package ru.yandex.intershop.controller.cart;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.*;

import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.OrderService;
import ru.yandex.intershop.service.PaymentService;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/cart/items")
public class CartController {

    private final CartService cartService;

    private final PaymentService paymentService;
    private final OrderService orderService;

    public CartController(CartService cartService, PaymentService paymentService, OrderService orderService) {
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> modifyItemCount(@PathVariable(name = "id") Long id,
                                        @RequestParam(name = "redirectUrl") String redirectUrl,
                                        @RequestPart(name = "action") String action
    ) {
        return cartService.modifyItemCountByItemId(id, Action.valueOf(action))
                .then(Mono.just("redirect:" + redirectUrl));
    }

    @PostMapping("/buy")
    public Mono<String> buy() {
        return cartService.findCartWithCartItemsById(1L)
                .flatMap(orderService::createOrderByCart)
                .flatMap(order -> Mono.just("redirect:/orders/" + order.getId() + "?newOrder=true"));
    }

    @GetMapping(value = {"", "/"})
    public Mono<String> cart(Model model) {
        return cartService.findCartWithCartItemsById(1L)
                .filter(Objects::nonNull)
                .flatMap(cart -> {
                    model.addAttribute("items", cart.getCartItems());
                    model.addAttribute("total", cart.getTotal());
                    model.addAttribute("empty", cart.getCartItems().isEmpty());
                    return paymentService.isBalanceEnough(cart.getTotal().floatValue())
                            .flatMap(isEnough -> {
                                model.addAttribute("isEnough", isEnough);
                                model.addAttribute("isUnavailable", false);
                                return Mono.just("cart");
                            })
                            .switchIfEmpty(Mono.defer(() -> {
                                model.addAttribute("isEnough", false);
                                model.addAttribute("isUnavailable", true);
                                return Mono.just("cart");
                            }));
                })
                .switchIfEmpty(Mono.defer(() -> {
                    model.addAttribute("items", List.of());
                    model.addAttribute("total", 0.0);
                    model.addAttribute("empty", true);
                    model.addAttribute("isEnough", false);
                    model.addAttribute("isUnavailable", true);
                    return Mono.just("cart");
                }));
    }

    }
