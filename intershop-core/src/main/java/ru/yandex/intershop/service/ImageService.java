package ru.yandex.intershop.service;


import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.repository.ImageRepository;


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
        return imageRepository.save(image);
    }
}
