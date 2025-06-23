package ru.yandex.intershop.controller.cart;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.yandex.intershop.model.*;
import ru.yandex.intershop.service.CartService;
import ru.yandex.intershop.service.ItemService;
import ru.yandex.intershop.service.OrderService;

import java.util.List;
import java.util.Optional;

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
    public String modifyItemCount(@PathVariable Long id,
                                  @RequestParam Action action,
                                  @RequestParam String redirectUrl) {
        cartService.modifyItemCountByItemId(id, action);
        return "redirect:" + redirectUrl;
    }

    @PostMapping("/buy")
    public String buy() {
        List<CartItem> cartItems = cartService.getItemsFromCart();
        Double total = cartItems.stream()
                .map(cartItem -> cartItem.getItemCount() * cartItem.getItem().getPrice())
                .reduce(Double::sum)
                .orElse(0.0);
        Order order = new Order(null, total, null);
        List<OrderItem> orderItems = cartItems.stream()
                .map(cartItem -> new OrderItem(null, order, cartItem.getItem(), cartItem.getItemCount(), cartItem.getItem().getPrice()))
                .toList();
        order.setOrderItems(orderItems);
        Order newOrder = orderService.save(order);
        return "redirect:/orders/" + newOrder.getId() + "?newOrder=true";
    }

    @GetMapping
    public String cart(Model model) {
        List<CartItem> cartItems = cartService.getItemsFromCart();
        Double total = cartItems.stream()
                .map(cartItem -> cartItem.getItemCount() * cartItem.getItem().getPrice())
                .reduce(Double::sum)
                .orElse(0.0);
        model.addAttribute("items", cartItems);
        model.addAttribute("total", total);
        model.addAttribute("empty", cartItems.isEmpty());
        return "cart";
    }

}
