package ru.yandex.intershop.service;


import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import ru.yandex.client.api.PaymentApi;
import ru.yandex.client.model.PaymentRequest;
import ru.yandex.intershop.exception.NotEnoughBalanceException;
import ru.yandex.intershop.exception.PaymentServiceUnavailableException;
import ru.yandex.intershop.model.User;

@Service
public class PaymentService {

    private final PaymentApi paymentApi;

    private final ReactiveOAuth2AuthorizedClientManager manager;

    public PaymentService(PaymentApi paymentApi,
                          ReactiveOAuth2AuthorizedClientManager manager) {
        this.paymentApi = paymentApi;
        this.manager = manager;
    }

    private Mono<String> getToken(){
        return manager.authorize(OAuth2AuthorizeRequest
                        .withClientRegistrationId("intershop")
                        .principal("system")
                        .build())
                .map(OAuth2AuthorizedClient::getAccessToken)
                .map(OAuth2AccessToken::getTokenValue);
    }

    public Mono<Boolean> isBalanceEnough(Long userId, float amount) {
        return getToken()
                .flatMap(token -> paymentApi.balanceGet(userId, token))
                .flatMap(balanceResponse -> Mono.just(balanceResponse.getBalance() >= amount))
                .onErrorResume(e -> Mono.empty());
    }

    public Mono<Boolean> buy(Long userId, float amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(amount);
        return getToken()
                .flatMap(accessToken -> paymentApi.paymentPost(userId, paymentRequest, accessToken))
                .flatMap(paymentResponse -> Mono.just(paymentResponse.getSuccess()))
                .onErrorMap(error -> {
                    if (error.getMessage().contains("Bad Request")) {
                        return new NotEnoughBalanceException("Недостаточно средств");
                    }
                    return new PaymentServiceUnavailableException("Платежный сервис недоступен");
                });
    }

}
