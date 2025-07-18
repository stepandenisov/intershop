package ru.yandex.intershop.service;


import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import ru.yandex.client.ApiClient;
import ru.yandex.client.api.DefaultApi;
import ru.yandex.client.model.BalanceResponse;

import java.util.concurrent.ExecutionException;

@Service
public class PaymentService {

    private final DefaultApi defaultApi;

    public PaymentService() {
        System.out.println("HERE4");
        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .filter((request, next) -> {
                    System.out.println("Sending: " + request.method() + " " + request.url());
                    return next.exchange(request);
                })
                .build();

        ApiClient apiClient = new ApiClient(webClient);
        this.defaultApi = new DefaultApi(apiClient);
    }

    public Mono<Void> buy() {
        return defaultApi.balanceGet()
                .doOnNext(balanceResponse -> System.out.println(balanceResponse.getBalance()))
                .then();
    }

}
