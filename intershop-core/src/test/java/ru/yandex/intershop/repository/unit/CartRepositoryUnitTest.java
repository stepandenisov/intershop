package ru.yandex.intershop.repository.unit;


import org.junit.jupiter.api.Test;
import ru.yandex.intershop.model.User;
import ru.yandex.intershop.model.cart.Cart;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class CartRepositoryUnitTest extends BaseRepositoryUnitTest{

    @Test
    void findById_shouldReturnCart(){
        User user = userRepository.save(new User(null, "user", "Password", "ROLE_ADMIN")).block();
        Cart cart = cartRepository.save(new Cart(null, 1.0, user.getId(), null)).block();
        assertNotNull(cart);
        Long cartId = cart.getId();
        Optional<Cart> actualCart = cartRepository.findById(cartId).blockOptional();

        assertTrue(actualCart.isPresent(), "Заказ должен быть");
        assertEquals(1.0, actualCart.get().getTotal(), "Общая сумма должна быть 1.0");
    }

}
