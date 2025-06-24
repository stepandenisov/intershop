package ru.yandex.intershop.repository.unit;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.yandex.intershop.model.cart.Cart;
import ru.yandex.intershop.repository.CartRepository;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class CartRepositoryUnitTest {

    @Autowired
    private CartRepository cartRepository;

    @BeforeEach
    public void setUp() {
        cartRepository.deleteAll();
    }

    @Test
    void findById_shouldReturnCart(){
        Cart cart = new Cart(null, 1.0, null);
        Long cartId = cartRepository.save(cart).getId();
        Optional<Cart> actualCart = cartRepository.findById(cartId);

        assertTrue(actualCart.isPresent(), "Заказ должен быть");
        assertEquals(1.0, actualCart.get().getTotal(), "Общая сумма должна быть 1.0");
    }

}
