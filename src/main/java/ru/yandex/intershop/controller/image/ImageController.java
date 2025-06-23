package ru.yandex.intershop.controller.image;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.service.ImageService;

@Controller
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    @GetMapping("/{itemId}")
    @ResponseBody
    public byte[] getImageByPostId(@PathVariable Long itemId){
        return imageService.getImageByItemId(itemId)
                .map(Image::getImageBytes)
                .orElse(null);
    }

}
