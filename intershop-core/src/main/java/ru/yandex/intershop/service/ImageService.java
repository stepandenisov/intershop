package ru.yandex.intershop.service;


import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.repository.ImageRepository;

import java.time.Duration;


@Service
public class ImageService {

    private final ImageRepository imageRepository;
    private final ReactiveRedisTemplate<String, Image> imageReactiveRedisTemplate;

    private static final String IMAGE_CACHE_KEY = "image:";

    public ImageService(ImageRepository imageRepository,
                        ReactiveRedisTemplate<String, Image> imageReactiveRedisTemplate){
        this.imageRepository = imageRepository;
        this.imageReactiveRedisTemplate = imageReactiveRedisTemplate;
    }

    public Mono<Image> getImageByItemId(Long itemId){
        return imageReactiveRedisTemplate.opsForValue().get(IMAGE_CACHE_KEY+itemId)
                .switchIfEmpty(imageRepository.findImageByItemId(itemId)
                        .flatMap(image -> imageReactiveRedisTemplate.opsForValue().set(IMAGE_CACHE_KEY+itemId, image, Duration.ofMinutes(1))
                                .then(Mono.just(image))));
    }

    public Mono<Image> save(Image image){
        return imageRepository.save(image);
    }
}
