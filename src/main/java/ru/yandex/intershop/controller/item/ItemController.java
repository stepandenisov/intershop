package ru.yandex.intershop.controller.item;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.*;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.model.item.Item;
import ru.yandex.intershop.model.item.ItemDto;
import ru.yandex.intershop.service.ItemService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/add")
    public Mono<String> addItemPage() {
        return Mono.just("add-item");
    }

    @PostMapping(value = {"/", ""}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public Mono<String> post(@RequestParam(name = "title") String title,
                       @RequestParam(name = "description") String description,
                       @RequestParam(name = "price") Double price,
                       @RequestPart(name = "image") MultipartFile imageBytes) throws IOException {
        Item item = new Item(null, title, description, price);
        byte[] rawBytes = imageBytes.getBytes();
        Byte[] byteArray = new Byte[rawBytes.length];
        Arrays.setAll(byteArray, n -> rawBytes[n]);
        Image image = new Image(null, null, byteArray);
        System.out.println("HERE 3");
        return itemService.saveItemWithImage(item, image)
                .flatMap(savedItemId -> Mono.just("redirect:/items/" + savedItemId));
    }

    @GetMapping("/{id}")
    public Mono<String> item(@PathVariable Long id, Model model) {
        return itemService.findItemByIdDto(id)
                .flatMap(item -> {
                    model.addAttribute("item", item);
                    return Mono.just("item");
                })
                .switchIfEmpty(Mono.defer(() -> Mono.just("redirect:/items")));
    }

    @GetMapping(value = {"/", ""})
    public Mono<String> items(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                              @RequestParam(name = "sort", required = false, defaultValue = "NO") Sorting sort,
                              @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                              @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                              Model model) {
        Paging paging = new Paging(pageNumber, pageSize, false, false);
        Flux<ItemDto> items = itemService.searchPaginatedAndSorted(search, paging, sort);

        model.addAttribute("items", items);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort.name());
        model.addAttribute("paging", paging);
        return Mono.just("items");
    }
}
