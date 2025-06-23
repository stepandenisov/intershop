package ru.yandex.intershop.controller.item;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.intershop.model.*;
import ru.yandex.intershop.service.ItemService;

import java.io.IOException;
import java.util.ArrayList;
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
    public String addItemPage() {
        return "add-item";
    }

    @PostMapping(value = {"/", ""}, consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_OCTET_STREAM_VALUE})
    public String post(@RequestParam(name = "title") String title,
                       @RequestParam(name = "description") String description,
                       @RequestParam(name = "price") Double price,
                       @RequestPart(name = "image") MultipartFile imageBytes) throws IOException {
        Item item = new Item(null, title, description, price);
        Image image = new Image(null, null, imageBytes.getBytes());
        Item savedItem = itemService.saveItemWithImage(item, image);
        return "redirect:/items/" + savedItem.getId();
    }

    @GetMapping("/{id}")
    public String item(@PathVariable Long id, Model model) {
        Optional<ItemDto> item = itemService.findItemByIdDto(id);
        if (item.isEmpty()) {
            return "redirect:/items";
        }
        model.addAttribute("item", item.get());
        return "item";
    }

    @GetMapping(value = {"/", ""})
    public String items(@RequestParam(name = "search", required = false, defaultValue = "") String search,
                        @RequestParam(name = "sort", required = false, defaultValue = "NO") Sorting sort,
                        @RequestParam(name = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        @RequestParam(name = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                        Model model) {
        Paging paging = new Paging(pageNumber, pageSize, false, false);
        List<ItemDto> items = itemService.searchPaginatedAndSorted(search, paging, sort);

        int N = 4;
        List<List<ItemDto>> reshapedItems = new ArrayList<>();
        for (int i = 0; i < items.size(); i += N) {
            int toIndex = Math.min(i + N, items.size());
            reshapedItems.add(items.subList(i, toIndex));
        }

        model.addAttribute("items", reshapedItems);
        model.addAttribute("search", search);
        model.addAttribute("sort", sort.name());
        model.addAttribute("paging", paging);
        return "items";
    }
}
