package ru.yandex.intershop.controller.item;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.csrf.WebSessionServerCsrfTokenRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.*;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.service.ItemService;
import ru.yandex.intershop.service.UserService;
import ru.yandex.intershop.service.auth.AuthService;

import java.util.Objects;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    private final AuthService authService;

    private final UserService userService;

    public ItemController(ItemService itemService, AuthService authService, UserService userService) {
        this.itemService = itemService;
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/add")
    public Mono<String> addItemPage() {
        return Mono.just("add-item");
    }

    @PostMapping(value = {"/", ""}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Mono<String> post(@RequestPart(name = "title") Mono<String> title,
                             @RequestPart(name = "description") Mono<String> description,
                             @RequestPart(name = "price") Mono<String> price,
                             @RequestPart(name = "image") Flux<FilePart> filePart) {
        Mono<Image> image = filePart.flatMap(Part::content)
                .flatMap(content -> Mono.just(content.asInputStream()))
                .flatMap(inputStream -> Mono.fromCallable(inputStream::readAllBytes))
                .flatMap(bytes -> Mono.just(new Image(null, null, bytes)))
                .single();
        Mono<Item> item = Mono.zip(title, description, price)
                .flatMap(tuple -> Mono.just(new Item(null, tuple.getT1(), tuple.getT2(), Double.parseDouble(tuple.getT3()))));
        return Mono.zip(item, image)
                .flatMap(tuple -> itemService.saveItemWithImage(tuple.getT1(), tuple.getT2()))
                .flatMap(savedItemId -> Mono.just("redirect:/items/" + savedItemId));
    }

    @GetMapping("/{id}")
    public Mono<String> item(@PathVariable("id") Long id, Model model, ServerWebExchange exchange) {
        return exchange.getPrincipal()
                .flatMap(principal -> userService.findUserByName(principal.getName())
                        .filter(Objects::nonNull)
                        .flatMap(user -> Mono.just(user.getId()))
                        .switchIfEmpty(Mono.just(-1L)))
                .switchIfEmpty(Mono.just(-1L))
                .flatMap(userId -> itemService.findItemByIdDto(id, userId)
                        .filter(Objects::nonNull)
                        .flatMap(item -> {
                            model.addAttribute("isAuthenticated", authService.isAuthenticated(exchange));
                            model.addAttribute("item", item);
                            return Mono.just("item");
                        })
                        .switchIfEmpty(Mono.just("redirect:/items")));
    }

    @GetMapping(value = {"/", ""})
    public Mono<String> items(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                              @RequestParam(name = "sort", required = false, defaultValue = "NO") String sort,
                              @RequestParam(name = "pageSize", required = false, defaultValue = "10") String pageSize,
                              @RequestParam(name = "pageNumber", required = false, defaultValue = "1") String pageNumber,
                              Model model, ServerWebExchange exchange) {
        Paging paging = new Paging(Integer.parseInt(pageNumber), Integer.parseInt(pageSize), false, false);
        return exchange.getPrincipal()
                .flatMap(principal -> userService.findUserByName(principal.getName())
                        .filter(Objects::nonNull)
                        .flatMap(user -> Mono.just(user.getId()))
                        .switchIfEmpty(Mono.just(-1L)))
                .switchIfEmpty(Mono.just(-1L))
                .flatMap(userId -> itemService.searchPaginatedAndSortedForUserById(search, paging, Sorting.valueOf(sort), userId)
                        .flatMap(items -> {
                            model.addAttribute("isAuthenticated", authService.isAuthenticated(exchange));
                            model.addAttribute("items", items);
                            model.addAttribute("search", search);
                            model.addAttribute("sort", sort);
                            model.addAttribute("paging", paging);
                            return Mono.just("items");
                        }));
    }
}
