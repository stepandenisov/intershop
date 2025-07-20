package ru.yandex.intershop.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.client.api.PaymentApi;

@Configuration
public class PaymentApiConfiguration {
    @Bean
    public PaymentApi paymentApi(){
        return new PaymentApi();
    }
}
