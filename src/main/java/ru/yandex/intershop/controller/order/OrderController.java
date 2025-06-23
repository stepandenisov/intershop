package ru.yandex.intershop.controller.order;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.intershop.model.Order;
import ru.yandex.intershop.service.OrderService;

import java.util.List;
import java.util.Optional;

@RequestMapping("/orders")
@Controller
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService){
        this.orderService = orderService;
    }

    @GetMapping("/{id}")
    public String order(@PathVariable Long id,
                        @RequestParam(required = false, defaultValue = "false") boolean newOrder,
                        Model model)
    {
        Optional<Order> order = orderService.findOrderById(id);
        if (order.isEmpty()){
            return "redirect:/orders";
        }
        model.addAttribute("order", order.get());
        model.addAttribute("newOrder", newOrder);
        return "order";
    }

    @GetMapping()
    public String orders(Model model)
    {
        List<Order> orders = orderService.findAll();
        model.addAttribute("orders", orders);
        return "orders";
    }
}
