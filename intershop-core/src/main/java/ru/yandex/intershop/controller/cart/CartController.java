package ru.yandex.intershop.controller.cart;

import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.*;

import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.OrderService;
import ru.yandex.intershop.service.PaymentService;
import ru.yandex.intershop.service.UserService;

import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/cart/items")
public class CartController {

    private final CartService cartService;

    private final UserService userService;

    private final PaymentService paymentService;
    private final OrderService orderService;

    public CartController(CartService cartService,
                          PaymentService paymentService,
                          OrderService orderService,
                          UserService userService) {
        this.cartService = cartService;
        this.paymentService = paymentService;
        this.orderService = orderService;
        this.userService = userService;
    }

    @PostMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public Mono<String> modifyItemCount(@PathVariable(name = "id") Long id,
                                        @RequestParam(name = "redirectUrl") String redirectUrl,
                                        @RequestPart(name = "action") String action,
                                        ServerWebExchange exchange
    ) {
        return exchange.getPrincipal()
                .flatMap(principal -> userService.findUserByName(principal.getName()))
                .flatMap(user -> cartService.modifyItemCountByItemId(user.getId(), id, Action.valueOf(action))
                        .then(Mono.just("redirect:" + redirectUrl)));
    }

    @PostMapping("/buy")
    public Mono<String> buy(ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .flatMap(principal -> userService.findUserByName(principal.getName()))
                .flatMap(user -> cartService.findCartWithCartItemsByUserId(user.getId()))
                .flatMap(orderService::createOrderByCart)
                .flatMap(order -> Mono.just("redirect:/orders/" + order.getId() + "?newOrder=true"));
    }

    @GetMapping(value = {"", "/"})
    @PostAuthorize("")
    public Mono<String> cart(Model model, ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .flatMap(principal -> userService.findUserByName(principal.getName()))
                .flatMap(user -> cartService.findCartWithCartItemsByUserId(user.getId()))
                .filter(Objects::nonNull)
                .flatMap(cart -> {
                    model.addAttribute("items", cart.getCartItems());
                    model.addAttribute("total", cart.getTotal());
                    model.addAttribute("empty", cart.getCartItems().isEmpty());
                    return paymentService.isBalanceEnough(cart.getUserId(), cart.getTotal().floatValue())
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
