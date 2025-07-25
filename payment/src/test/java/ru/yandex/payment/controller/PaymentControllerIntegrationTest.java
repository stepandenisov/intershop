package ru.yandex.payment.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import ru.yandex.payment.model.PaymentRequest;

import java.util.Map;

import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

public class PaymentControllerIntegrationTest extends BaseControllerIntegrationTest {

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void getBalance_shouldReturnOk() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/payment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest(1.0F))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    @WithAnonymousUser
    void getBalance_shouldReturn403() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/payment/1")
                .exchange()
                .expectStatus().is4xxClientError();
    }

    @Test
    @WithMockUser(username = "admin",
            roles = {"ADMIN"})
    void getBalance_shouldReturnBadRequest() {
        webTestClient
                .mutateWith(csrf())
                .post()
                .uri("/payment/1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(new PaymentRequest(10000.0F))
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest();
    }

}
