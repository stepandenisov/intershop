package ru.yandex.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import ru.yandex.payment.model.PaymentRequest;

import java.util.Map;

public class PaymentControllerIntegrationTest extends BaseControllerIntegrationTest {

    @Test
    void getBalance_shouldReturnOk() {
        webTestClient.post()
                .uri("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest(1.0F))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getBalance_shouldReturnBadRequest() {
        webTestClient.post()
                .uri("/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest(10000.0F))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

}
