package ru.yandex.intershop.controller.item;


import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.yandex.intershop.model.Image;
import ru.yandex.intershop.model.Item;
import ru.yandex.intershop.service.ItemService;

import java.io.IOException;
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
                       @RequestParam(name="count") Integer count,
                       @RequestPart(name="image") MultipartFile imageBytes) throws IOException {
        Image image = new Image(null, null, imageBytes.getBytes());
        Item item = new Item(null, title, description, price, count);
        Item savedItem = itemService.saveItemWithImage(item, image);
        return "redirect:/items/" + savedItem.getId();
    }

    @GetMapping("/{id}")
    public String getImageById(@PathVariable Long id, Model model){
         Optional<Item> item = itemService.findItemById(id);
         if (item.isEmpty()){
             return "redirect:/items";
         }
         model.addAttribute("item", item.get());
         return "item";
    }
}
