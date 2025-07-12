package ru.yandex.intershop.controller.integration;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.intershop.repository.*;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseControllerIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ImageRepository imageRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll()
                .then(cartItemRepository.deleteAll())
                .then(imageRepository.deleteAll())
                .then(orderRepository.deleteAll())
                .then(cartRepository.deleteAll())
                .then(itemRepository.deleteAll())
                .block();
    }

}
