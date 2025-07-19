package ru.yandex.payment.controller;


import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Generated;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import ru.yandex.payment.model.PaymentRequest;
import ru.yandex.payment.model.PaymentResponse;
import ru.yandex.payment.service.BalanceService;

@Generated(value = "org.openapitools.codegen.languages.SpringCodegen", date = "2025-07-19T20:28:07.083674400+04:00[Europe/Samara]", comments = "Generator version: 7.12.0")
@Controller
@RequestMapping("${openapi..base-path:}")
public class PaymentApiController implements PaymentApi {

    private final BalanceService balanceService;

    public PaymentApiController(BalanceService balanceService) {
        this.balanceService = balanceService;
    }

    @Override
    public Mono<ResponseEntity<PaymentResponse>> paymentPost(
            @Parameter(name = "PaymentRequest", required = true) @Valid @RequestBody Mono<PaymentRequest> paymentRequest,
            @Parameter(hidden = true) final ServerWebExchange exchange
    ) {
        Mono<Void> result;
        exchange.getResponse().setStatusCode(HttpStatus.SERVICE_UNAVAILABLE);
        for (MediaType mediaType : exchange.getRequest().getHeaders().getAccept()) {
            if (mediaType.isCompatibleWith(MediaType.valueOf("application/json"))) {
                result = paymentRequest.flatMap(pR -> {
                    float amount = pR.getAmount();
                    if (amount <= balanceService.getBalance()) {
                        balanceService.changeBalance(amount);
                        exchange.getResponse().setStatusCode(HttpStatus.OK);
                        String exampleString = "{ \"success\" : true, \"remainingBalance\" : " + balanceService.getBalance() + " }";
                        return ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                    }
                    exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
                    String exampleString = "{ \"success\" : false, \"remainingBalance\" : " + balanceService.getBalance() + ", \"error\" : \"Недостаточно средств для оплаты\" }";
                    return ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
                });
                return result.then(Mono.empty());
            }
        }
        String exampleString = "{ \"error\" : \"Сервис платежей временно недоступен\" }";
        result = ApiUtil.getExampleResponse(exchange, MediaType.valueOf("application/json"), exampleString);
        return result.then(paymentRequest).then(Mono.empty());
    }
}
