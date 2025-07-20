package ru.yandex.intershop.service.unit;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.yandex.client.api.PaymentApi;
import ru.yandex.client.model.BalanceResponse;
import ru.yandex.client.model.PaymentRequest;
import ru.yandex.client.model.PaymentResponse;
import ru.yandex.intershop.service.PaymentService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PaymentServiceUnitTest {

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private PaymentApi paymentApi;

    @Test
    void getBalance_shouldReturnBalance() {
        BalanceResponse balanceResponse=new BalanceResponse();
        balanceResponse.setBalance(10F);
        when(paymentApi.balanceGet()).thenReturn(Mono.just(balanceResponse));
        Boolean isEnough = paymentService.isBalanceEnough(1.0F).block();
        assertNotNull(isEnough, "Должно быть не null");
        assertTrue(isEnough, "Должно быть true");
    }

    @Test
    void buy_shouldReturnTrue() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(1.0F);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setSuccess(true);
        when(paymentApi.paymentPost(paymentRequest)).thenReturn(Mono.just(paymentResponse));
        Boolean isSuccess = paymentService.buy(1.0F).block();
        assertNotNull(isSuccess, "Должно быть не null");
        assertTrue(isSuccess, "Должно быть true");
    }

}
