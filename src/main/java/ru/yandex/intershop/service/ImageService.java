package ru.yandex.intershop.service;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.repository.ImageRepository;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    public Mono<Image> getImageByItemId(Long itemId){
        return imageRepository.findImageByItemId(itemId);
    }

    public Mono<Image> save(Image image){
        System.out.println("HERE 2");
        return imageRepository.save(image);
    }
}
