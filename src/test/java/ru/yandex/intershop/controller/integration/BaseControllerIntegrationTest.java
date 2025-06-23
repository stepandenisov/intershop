package ru.yandex.intershop.controller.integration;


import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.intershop.controller.item.ItemController;
import ru.yandex.intershop.repository.CartRepository;
import ru.yandex.intershop.repository.ImageRepository;
import ru.yandex.intershop.repository.ItemRepository;
import ru.yandex.intershop.repository.OrderRepository;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public abstract class BaseControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

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
