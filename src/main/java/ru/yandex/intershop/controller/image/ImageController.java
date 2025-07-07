package ru.yandex.intershop.controller.image;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.image.Image;
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
    public Flux<Byte> getImageByPostId(@PathVariable Long itemId){
        return imageService.getImageByItemId(itemId)
                .flatMapMany(image -> Flux.fromIterable(Arrays.asList(image.getImageBytes())))
                .switchIfEmpty(Flux.defer(Flux::empty));
    }

}
