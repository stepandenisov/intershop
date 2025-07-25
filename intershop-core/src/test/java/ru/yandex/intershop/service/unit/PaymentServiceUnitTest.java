package ru.yandex.intershop.service.unit;


import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;
import ru.yandex.client.api.PaymentApi;
import ru.yandex.client.model.BalanceResponse;
import ru.yandex.client.model.PaymentRequest;
import ru.yandex.client.model.PaymentResponse;
import ru.yandex.intershop.service.PaymentService;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
public class PaymentServiceUnitTest {

    @Autowired
    private PaymentService paymentService;

    @MockitoBean
    private PaymentApi paymentApi;

    @MockitoBean
    private ReactiveOAuth2AuthorizedClientManager manager;


    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void getBalance_shouldReturnBalance() {
        BalanceResponse balanceResponse = new BalanceResponse();
        balanceResponse.setBalance(10F);
        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "TOKEN", Instant.now(), Instant.now().plusSeconds(10));
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("ID")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientId("ID")
                .tokenUri("ID")
                .build();
        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(registration, "Name", token);
        when(manager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(Mono.just(client));
        when(paymentApi.balanceGet(1L, "TOKEN")).thenReturn(Mono.just(balanceResponse));
        Boolean isEnough = paymentService.isBalanceEnough(1L, 1.0F).block();
        assertNotNull(isEnough, "Должно быть не null");
        assertTrue(isEnough, "Должно быть true");
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void buy_shouldReturnTrue() {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(1.0F);
        PaymentResponse paymentResponse = new PaymentResponse();
        paymentResponse.setSuccess(true);
        OAuth2AccessToken token = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "TOKEN", Instant.now(), Instant.now().plusSeconds(10));
        ClientRegistration registration = ClientRegistration
                .withRegistrationId("ID")
                .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
                .clientId("ID")
                .tokenUri("ID")
                .build();
        OAuth2AuthorizedClient client = new OAuth2AuthorizedClient(registration, "Name", token);
        when(manager.authorize(any(OAuth2AuthorizeRequest.class))).thenReturn(Mono.just(client));
        when(paymentApi.paymentPost(1L, paymentRequest, "TOKEN")).thenReturn(Mono.just(paymentResponse));
        Boolean isSuccess = paymentService.buy(1L, 1.0F).block();
        assertNotNull(isSuccess, "Должно быть не null");
        assertTrue(isSuccess, "Должно быть true");
    }

}
