package ru.yandex.intershop.service.integration;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.intershop.repository.CartRepository;
import ru.yandex.intershop.repository.ImageRepository;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.repository.OrderRepository;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseServiceIntegrationTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ImageRepository imageRepository;

    @BeforeEach
    @Transactional
    void setUp() {
        orderRepository.deleteAll();
        imageRepository.deleteAll();
        cartRepository.deleteAll();
        itemRepository.deleteAll();
    }

}
