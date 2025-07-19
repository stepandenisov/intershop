package ru.yandex.intershop.service;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.yandex.client.ApiClient;
import ru.yandex.client.api.PaymentApi;
import ru.yandex.client.model.PaymentRequest;
import ru.yandex.intershop.exception.NotEnoughBalanceException;
import ru.yandex.intershop.exception.PaymentServiceUnavailableException;

@Service
public class PaymentService {

    private final PaymentApi paymentApi;

    public PaymentService() {
        this.paymentApi = new PaymentApi();
    }

    public Mono<Boolean> checkBalanceIsEnough(float amount){
        return paymentApi.balanceGet()
                .flatMap(balanceResponse -> Mono.just(balanceResponse.getBalance() >= amount))
                .onErrorReturn(false);
    }

    public Mono<Boolean> buy(float amount) {
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setAmount(amount);
        return paymentApi.paymentPost(paymentRequest)
                .flatMap(paymentResponse -> Mono.just(paymentResponse.getSuccess()))
                .onErrorMap(error -> {
                    if (error.getMessage().contains("Bad Request")){
                        return new NotEnoughBalanceException("Недостаточно средств");
                    }
                    return new PaymentServiceUnavailableException("Платежный сервис недоступен");
                });
    }

}
