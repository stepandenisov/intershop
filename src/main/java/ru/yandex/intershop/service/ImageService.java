package ru.yandex.intershop.service;


import org.springframework.stereotype.Service;
import ru.yandex.intershop.model.image.Image;
import ru.yandex.intershop.repository.ImageRepository;

import java.util.Optional;

@Service
public class ImageService {

    private final ImageRepository imageRepository;

    public ImageService(ImageRepository imageRepository){
        this.imageRepository = imageRepository;
    }

    public Optional<Image> getImageByItemId(Long itemId){
        return imageRepository.findImageByItemId(itemId);
    }

    public void save(Image image){
        imageRepository.save(image);
    }
}
