package ru.yandex.intershop.exception;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.reactive.result.view.Rendering;

@ControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler({NotEnoughBalanceException.class, PaymentServiceUnavailableException.class})
    public Rendering handleNotEnoughBalance(Exception ex) {
        return Rendering.view("error")
                .modelAttribute("message", ex.getMessage())
                .build();
    }

}
