package ru.yandex.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;


public class BalanceControllerIntegrationTest extends BaseControllerIntegrationTest {


    @Test
    void getBalance_shouldReturnOk() {
        webTestClient.get()
                .uri("/balance")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void getBalance_shouldReturnUnavailable() {
        webTestClient.get()
                .uri("/balance")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}