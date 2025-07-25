package ru.yandex.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;


public class BalanceControllerIntegrationTest extends BaseControllerIntegrationTest {


    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void getBalance_shouldReturnOk() {
        webTestClient.get()
                .uri("/balance/1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    void getBalance_shouldReturn403() {
        webTestClient.get()
                .uri("/balance/1")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void getBalance_shouldReturnUnavailable() {
        webTestClient.get()
                .uri("/balance/1")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}