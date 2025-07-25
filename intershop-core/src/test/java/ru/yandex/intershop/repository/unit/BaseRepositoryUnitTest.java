package ru.yandex.intershop.repository.unit;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import ru.yandex.intershop.repository.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseRepositoryUnitTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ImageRepository imageRepository;

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    OrderItemRepository orderItemRepository;

    @Autowired
    UserRepository userRepository;


    @BeforeEach
    public void setUp() {
        cartItemRepository.deleteAll()
                .then(orderItemRepository.deleteAll())
                .then(imageRepository.deleteAll())
                .then(orderRepository.deleteAll())
                .then(itemRepository.deleteAll())
                .then(cartRepository.deleteAll())
                .then(userRepository.deleteAll())
                .block();
    }
}
