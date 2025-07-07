package ru.yandex.intershop.repository;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.intershop.model.image.Image;

@Repository
public interface ImageRepository extends ReactiveCrudRepository<Image, Long> {

    Mono<Image> findImageByItemId(Long itemId);

}
