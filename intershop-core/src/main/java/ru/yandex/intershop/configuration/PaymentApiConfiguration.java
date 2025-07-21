package ru.yandex.intershop.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.client.ApiClient;
import ru.yandex.client.api.PaymentApi;

@Configuration
public class PaymentApiConfiguration {

    @Value("${payment.url}")
    private String baseUrl;

    @Bean
    public ApiClient paymentApiClient() {
        return new ApiClient(baseUrl);
    }

    @Bean
    public PaymentApi paymentApi(ApiClient apiClient){
        return new PaymentApi(apiClient);
    }
}
