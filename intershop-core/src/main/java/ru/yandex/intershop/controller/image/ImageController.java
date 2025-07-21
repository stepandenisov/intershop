package ru.yandex.intershop.controller.image;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import ru.yandex.intershop.service.ImageService;

import java.util.Arrays;

@Controller
@RequestMapping("/images")
public class ImageController {

    private final ImageService imageService;

    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    @GetMapping("/{itemId}")
    @ResponseBody
    public Flux<byte[]> getImageByPostId(@PathVariable("itemId") Long itemId){
        return imageService.getImageByItemId(itemId)
                .flatMapMany(image -> Flux.fromIterable(Arrays.asList(image.getImageBytes())))
                .switchIfEmpty(Flux.defer(Flux::empty));
    }

}
