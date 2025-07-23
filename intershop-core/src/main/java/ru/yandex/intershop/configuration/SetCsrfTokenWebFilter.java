package ru.yandex.intershop.configuration;

import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class SetCsrfTokenWebFilter implements WebFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        return exchange.<Mono<CsrfToken>>getAttribute(CsrfToken.class.getName())
                .switchIfEmpty(Mono.empty())
                .doOnNext(token -> exchange.getAttributes().put("_csrf", token))
                .then(chain.filter(exchange));
    }
}
