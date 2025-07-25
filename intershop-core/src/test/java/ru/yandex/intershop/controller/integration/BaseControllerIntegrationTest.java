package ru.yandex.intershop.controller.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.intershop.configuration.EmbeddedRedisConfiguration;
import ru.yandex.intershop.configuration.RedisConfiguration;
import ru.yandex.intershop.repository.*;
import ru.yandex.intershop.service.auth.AuthService;

@AutoConfigureWebTestClient
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.properties")
@DirtiesContext
@Import(EmbeddedRedisConfiguration.class)
public abstract class BaseControllerIntegrationTest{

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

    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        orderItemRepository.deleteAll()
                .then(cartItemRepository.deleteAll())
                .then(imageRepository.deleteAll())
                .then(orderRepository.deleteAll())
                .then(cartRepository.deleteAll())
                .then(itemRepository.deleteAll())
                .then(userRepository.deleteAll())
                .block();
    }

}
